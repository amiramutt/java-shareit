package ru.practicum.shareit.request;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(USER_ID) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @PostMapping
    ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID) long userId,
                                             @Valid @RequestBody ItemRequestDto request) {
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping
    ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(USER_ID) long userId) {
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }
}
