package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестный статус бронирования: " + stateParam));
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookingStatus(
			@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
			@PathVariable Long bookingId,
			@RequestParam(defaultValue = "true") Boolean approved) {
		return bookingClient.updateBookingStatus(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(
			@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
			@RequestParam(value = "state", defaultValue = "ALL") String bookingState,
			@RequestParam(value = "from", defaultValue = "0") Integer from,
			@RequestParam(value = "size", defaultValue = "10") Integer size
	) {
		BookingState state = BookingState.from(bookingState)
				.orElseThrow(() -> new IllegalArgumentException("Неизвестный статус бронирования: " + bookingState));
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

}
