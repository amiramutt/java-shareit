package ru.practicum.shareit.item.storage;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public Item addItem(Item item) {
        if (item.getId() == null) {
            item.setId(currentId.incrementAndGet());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> updateItem(Item itemToUpdate) {
        if (itemToUpdate.getId() == null || !items.containsKey(itemToUpdate.getId())) {
            return Optional.empty();
        }

        items.put(itemToUpdate.getId(), itemToUpdate);
        return Optional.of(itemToUpdate);
    }

    @Override
    public void deleteItemById(Long itemId) {
        items.remove(itemId);
    }
}
