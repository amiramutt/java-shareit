package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long requesterId, ItemRequestCreateDto createDto);

    List<ItemRequestDto> getItemRequestsByUserId(long requesterId);

    List<ItemRequestDto> findAllByNotRequesterIdSorted(long requesterId);

    ItemRequestDto getItemRequestById(long itemRequestId);
}
