package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBookingDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void serializeAndDeserialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 2, 12, 0);

        CreateBookingDto dto = new CreateBookingDto(start, end, 1L);

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("2026-01-01T12:00:00");

        CreateBookingDto parsed = objectMapper.readValue(json, CreateBookingDto.class);
        assertThat(parsed.getItemId()).isEqualTo(1L);
        assertThat(parsed.getStart()).isEqualTo(start);
        assertThat(parsed.getEnd()).isEqualTo(end);
    }
}

