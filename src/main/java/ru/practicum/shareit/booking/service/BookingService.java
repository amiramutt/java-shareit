package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto bookingDto, Long userId);

    BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state);
}
