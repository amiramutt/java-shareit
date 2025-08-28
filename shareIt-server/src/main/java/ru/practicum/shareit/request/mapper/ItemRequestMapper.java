package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    private ItemRequestMapper() {
    }
    
    public static ItemRequest itemRequestCreateDtoToItemRequest(ItemRequestCreateDto createDto) {
        return ItemRequest.builder()
                .description(createDto.getDescription())
                .dateCreated(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toUserDto(itemRequest.getRequester()))
                .created(itemRequest.getDateCreated())
                .items(ItemMapper.toListDto(itemRequest.getItems()))
                .build();
    }

    public static List<ItemRequestDto> toListItemRequestDto(List<ItemRequest> requests) {
        List<ItemRequestDto> listDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            listDto.add(toItemRequestDto(request));
        }
        return listDto;
    }
}
