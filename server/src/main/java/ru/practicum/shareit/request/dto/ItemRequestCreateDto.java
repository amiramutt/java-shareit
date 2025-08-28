package ru.practicum.shareit.request.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemRequestCreateDto {
    private String description;
}
