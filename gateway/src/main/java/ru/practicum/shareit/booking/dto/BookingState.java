package ru.practicum.shareit.booking.dto;

import java.util.Optional;

/**
 * ALL - все
 * CURRENT - Текущие
 * FUTURE - Будущие
 * PAST - Завершенные
 * REJECTED - Отклоненные
 * WAITING - Ожидающие подтверждения
 */

public enum BookingState {
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
