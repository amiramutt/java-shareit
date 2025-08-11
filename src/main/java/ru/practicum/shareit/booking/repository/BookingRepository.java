package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // Для владельца вещей
    //@Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    //@Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start_date < :now AND b.end_date > :now ORDER BY b.start_date DESC")
    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    //@Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end_date < :now ORDER BY b.start_date DESC")
    List<Booking> findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    //@Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start_date > :now ORDER BY b.start_date DESC")
    List<Booking> findByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    //@Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start_date DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndDateBefore(Long bookerId, Long itemId,
                                                               BookingStatus status, LocalDateTime end);
}