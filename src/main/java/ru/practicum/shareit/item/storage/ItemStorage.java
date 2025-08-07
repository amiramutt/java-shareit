package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllItemsByOwnerId(Long ownerId);

    List<Item> getAvailableItemsByText(String text);

    Optional<Item> updateItem(Item item);

    void deleteItemById(Long itemId);
}
