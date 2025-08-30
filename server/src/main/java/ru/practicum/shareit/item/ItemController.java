package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @Validated
    public CreateItemDto addItem(@RequestHeader(USER_ID) Long userId,
                                 @Valid @RequestBody CreateItemDto createItemDto) {
        return itemService.addItem(userId, createItemDto);
    }

    @PatchMapping("/{itemId}")
    public CreateItemDto updateItem(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody UpdateItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingDto> getAllItemsByUserId(@RequestHeader(USER_ID) Long userId) {
        return itemService.getAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<CreateItemDto> getAvailableItemsByText(@RequestParam String text) {
        return itemService.getAvailableItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}