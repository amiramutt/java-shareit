package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndDateBefore(Long bookerId, Long itemId,
                                                               BookingStatus status, LocalDateTime end);
}