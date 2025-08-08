package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    CreateItemDto addItem(Long ownerId, CreateItemDto createItemDto);

    CreateItemDto updateItem(Long ownerId, Long itemId, UpdateItemDto itemDto);

    CreateItemDto getItemById(Long itemId);

    List<CreateItemDto> getAllItemsByOwnerId(Long ownerId);

    List<CreateItemDto> getAvailableItemsByText(String text);
}
