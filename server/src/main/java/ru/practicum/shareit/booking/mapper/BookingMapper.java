package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking, User user, Item item, BookingStatus status) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .booker(UserMapper.toUserDto(user))
                .item(ItemMapper.toItemDto(item))
                .status(status)
                .build();

    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(CreateBookingDto bookingDto, User user, Item item, BookingStatus status) {
        if (bookingDto == null) {
            return null;
        }
        return Booking.builder()
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .status(status)
                .build();
    }
}
