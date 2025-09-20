package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingTest {

    @Autowired
    private JacksonTester<Booking> json;

    @Test
    void serializeAndDeserialize() throws Exception {
        User booker = new User();
        booker.setId(5L);

        Item item = new Item();
        item.setId(3L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.of(2026, 1, 1, 12, 0));
        booking.setEndDate(LocalDateTime.of(2026, 1, 2, 12, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        String content = json.write(booking).getJson();
        assertThat(content).contains("2026-01-01T12:00:00");
        assertThat(content).contains("2026-01-02T12:00:00");
        assertThat(content).contains("\"status\":\"APPROVED\"");

        String jsonStr = "{\"id\":2,\"startDate\":\"2026-01-05T10:00:00\",\"endDate\":\"2026-01-06T10:00:00\",\"status\":\"WAITING\"}";
        Booking parsed = json.parseObject(jsonStr);
        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}


