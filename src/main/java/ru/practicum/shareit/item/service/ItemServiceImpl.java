package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public CreateItemDto addItem(Long ownerId, CreateItemDto createItemDto) {
        User owner = userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item item = ItemMapper.toItem(createItemDto, owner);
        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public CreateItemDto updateItem(Long ownerId, Long itemId, UpdateItemDto newItemDto) {
        userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item existingItem = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не является владельцем вещи с ID " + itemId);
        }

        if (newItemDto.getName() != null && !newItemDto.getName().isBlank()) {
            existingItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank()) {
            existingItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            existingItem.setAvailable(newItemDto.getAvailable());
        }

        Item updatedItem = itemStorage.updateItem(existingItem)
                .orElseThrow(() -> new NotFoundException("Не удалось обновить вещь с ID " + itemId));
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public CreateItemDto getItemById(Long itemId) {
        Item item = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<CreateItemDto> getAllItemsByOwnerId(Long ownerId) {
        userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден."));

        return itemStorage.getAllItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreateItemDto> getAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.getAvailableItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}