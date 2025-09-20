package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private ItemRequestRepository itemRequestRepository;

    @InjectMocks private ItemServiceImpl itemService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "User", "user@mail.com");
        item = new Item(1L, "item", "desc", true, user, null);
    }

    @Test
    void addItemWithRequest() {
        CreateItemDto dto = new CreateItemDto(null, "item", "desc", true, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateItemDto result = itemService.addItem(1L, dto);

        assertThat(result.getName()).isEqualTo("item");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItemUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.addItem(99L, new CreateItemDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItemSuccess() {
        UpdateItemDto update = new UpdateItemDto(null, "NewName", "NewDesc", false, 99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateItemDto result = itemService.updateItem(1L, 1L, update);

        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.getDescription()).isEqualTo("NewDesc");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItemWrongOwner() {
        User other = new User(2L, "another", "another@mail.com");
        item.setOwner(other);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(1L, 1L, new UpdateItemDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemByIdSuccess() {
        Comment comment = new Comment(1L, "comment1", item, user, LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(comment));

        ItemWithBookingDto result = itemService.getItemById(1L);

        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("comment1");
    }

    @Test
    void getAllItemsByOwnerIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdInAndStatus(any(), any()))
                .thenReturn(List.of(new Booking(1L, LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED)));
        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of());

        List<ItemWithBookingDto> result = itemService.getAllItemsByOwnerId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastBooking()).isNotNull();
    }

    @Test
    void getAvailableItemsByTextReturnsEmpty() {
        List<CreateItemDto> result = itemService.getAvailableItemsByText("   ");
        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }

    @Test
    void addCommentSuccess() {
        CommentDto dto = new CommentDto(null, "comment2", null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(
                eq(1L), eq(1L), eq(BookingStatus.APPROVED), any()))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CommentDto result = itemService.addComment(1L, 1L, dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("comment2");
    }

    @Test
    void addCommentThrowsValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(any(), any(), any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> itemService.addComment(1L, 1L, new CommentDto()))
                .isInstanceOf(ValidationException.class);
    }
}
