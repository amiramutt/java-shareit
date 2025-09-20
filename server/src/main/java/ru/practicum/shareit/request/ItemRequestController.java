package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestCreateDto createDto) {
        return itemRequestService.createItemRequest(userId, createDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsByNotUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllByNotRequesterIdSorted(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
