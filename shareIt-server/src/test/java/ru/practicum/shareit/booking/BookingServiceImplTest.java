package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "owner", "owner@mail.com");
        booker = new User(2L, "booker", "booker@mail.com");
        item = new Item(1L, "item1", "desc1", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), item, booker, BookingStatus.WAITING);
    }

    @Test
    void createBookingSuccess() {
        CreateBookingDto dto = new CreateBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.createBooking(dto, booker.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBookingThrowUserNotFound() {
        CreateBookingDto dto = new CreateBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBookingThrowItemNotFound() {
        CreateBookingDto dto = new CreateBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), 99L);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBookingUnavailableThrowValidationException() {
        item.setAvailable(false);
        CreateBookingDto dto = new CreateBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBookingThrowValidationException() {
        CreateBookingDto dto = new CreateBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(dto, owner.getId()));
    }

    @Test
    void updateBookingStatusSuccess() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.updateBookingStatus(booking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateBookingStatusThrowValidationException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.updateBookingStatus(booking.getId(), true, 99L));
    }

    @Test
    void getBookingByIdSuccess() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(booking.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingByIdThrowValidationException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(booking.getId(), 99L));
    }

    @Test
    void getUserBookingsSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDateDesc(booker.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookingsSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDateDesc(owner.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }
}
