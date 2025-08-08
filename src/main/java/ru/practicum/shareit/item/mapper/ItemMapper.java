package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    private ItemMapper() {
    }

    public static CreateItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return CreateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(CreateItemDto createItemDto, User owner) {
        if (createItemDto == null) {
            return null;
        }
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .available(createItemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Item toItem(UpdateItemDto updateItemDto, User owner) {
        if (updateItemDto == null) {
            return null;
        }
        return Item.builder()
                .id(updateItemDto.getId())
                .name(updateItemDto.getName())
                .description(updateItemDto.getDescription())
                .available(updateItemDto.getAvailable())
                .owner(owner)
                .build();
    }
}
