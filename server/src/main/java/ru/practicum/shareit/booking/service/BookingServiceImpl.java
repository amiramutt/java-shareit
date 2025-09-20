package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(CreateBookingDto bookingDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена."));

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неверные даты бронирования.");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь в данный момент недоступна.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Владелец не может забронировать свою собственную вещь.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, user, item, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking, user, item, BookingStatus.WAITING);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено."));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Только владелец может подтвердить бронирование");
        }
        User booker = userRepository.findById(booking.getBooker().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + booking.getItem().getId() + " не найдена."));

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено."));
        if (!booking.getBooker().getId().equals(ownerId) &&
                !booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Доступ запрещен.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDateDesc(userId);
                break;
            case CURRENT:
                bookings =  bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(userId, now, now);
                break;
            case PAST:
                bookings =  bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(userId, now);
                break;
            case FUTURE:
                bookings =  bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(userId, now);
                break;
            case WAITING:
                bookings =  bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings =  bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new NotFoundException("Неизвестное состояние бронирования: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDateDesc(ownerId);
                break;
            case CURRENT:
                bookings =  bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(ownerId, now, now);
                break;
            case PAST:
                bookings =  bookingRepository.findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(ownerId, now);
                break;
            case FUTURE:
                bookings =  bookingRepository.findByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(ownerId, now);
                break;
            case WAITING:
                bookings =  bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings =  bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new NotFoundException("Неизвестное состояние бронирования: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}

