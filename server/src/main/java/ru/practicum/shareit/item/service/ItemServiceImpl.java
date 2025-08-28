package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItemWithBookingDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public CreateItemDto addItem(Long ownerId, CreateItemDto createItemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item item = ItemMapper.toItem(createItemDto, owner);

        if (createItemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(createItemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с ID " + createItemDto.getRequestId() + " не найден."));
            item.setRequest(itemRequest);
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public CreateItemDto updateItem(Long ownerId, Long itemId, UpdateItemDto newItemDto) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не является владельцем вещи с ID " + itemId);
        }

        if (newItemDto.getName() != null && !newItemDto.getName().isBlank()) {
            existingItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank()) {
            existingItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            existingItem.setAvailable(newItemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена."));
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentsDto = comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemWithBookingDto(item, null, null, commentsDto);
    }

    @Override
    public List<ItemWithBookingDto> getAllItemsByOwnerId(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

                    BookingDto lastBooking = itemBookings.stream()
                            .filter(b -> b.getStartDate().isBefore(now))
                            .max(Comparator.comparing(Booking::getStartDate))
                            .map(b -> BookingMapper.toBookingDto(b, b.getBooker(), b.getItem(), b.getStatus()))
                            .orElse(null);

                    BookingDto nextBooking = itemBookings.stream()
                            .filter(b -> b.getStartDate().isAfter(now))
                            .min(Comparator.comparing(Booking::getStartDate))
                            .map(b -> BookingMapper.toBookingDto(b, b.getBooker(), b.getItem(), b.getStatus()))
                            .orElse(null);

                    List<CommentDto> itemComments = commentsByItemId
                            .getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(ItemMapper::toCommentDto)
                            .toList();

                    return toItemWithBookingDto(item, lastBooking, nextBooking, itemComments);
                })
                .toList();
    }

    @Override
    public List<CreateItemDto> getAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.getAvailableItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена."));

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("Пользователь с ID " + userId + " никогда не бронировал вещь с ID " + itemId);
        }

        Comment comment = ItemMapper.toComment(commentDto, author, item);
        Comment savedComment = commentRepository.save(comment);
        return ItemMapper.toCommentDto(savedComment);
    }
}