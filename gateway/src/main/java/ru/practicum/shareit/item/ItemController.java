package ru.practicum.shareit.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItemsByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text) {
        return itemClient.getAvailableItemsByText(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название товара не может быть пустым или содержать только пробелы");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание товара не может быть пустым или содержать только пробелы");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус о том, доступна или нет вещь для аренды обязателен");
        }
        return itemClient.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody ItemDto item) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentCreateDto comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
