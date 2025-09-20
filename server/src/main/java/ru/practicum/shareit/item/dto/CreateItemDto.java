package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {

    Long id;

    @NotBlank(message = "Название товара не может быть пустым или содержать только пробелы")
    String name;

    @NotBlank(message = "Описание товара не может быть пустым или содержать только пробелы")
    String description;

    @NotNull(message = "Статус о том, доступна или нет вещь для аренды обязателен")
    Boolean available;
    Long requestId;
}
