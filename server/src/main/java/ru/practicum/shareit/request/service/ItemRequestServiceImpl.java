package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(long requesterId, ItemRequestCreateDto createDto) {
        User requester = checkAndReturnUser(requesterId);
        ItemRequest itemRequest = ItemRequestMapper.itemRequestCreateDtoToItemRequest(createDto);
        itemRequest.setRequester(requester);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUserId(long requesterId) {
        checkAndReturnUser(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByDateCreatedDesc(requesterId);
        if (requests.isEmpty()) {
            throw new NotFoundException("У пользователя с ID " + requesterId + " нет запросов");
        }
        return ItemRequestMapper.toListItemRequestDto(requests);
    }

    @Override
    public List<ItemRequestDto> findAllByNotRequesterIdSorted(long requesterId) {
        checkAndReturnUser(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByDateCreatedDesc(requesterId);
        if (requests.isEmpty()) {
            throw new NotFoundException("У пользователя с ID " + requesterId + " нет запросов");
        }
        return ItemRequestMapper.toListItemRequestDto(requests);
    }

    @Override
    public ItemRequestDto getItemRequestById(long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User checkAndReturnUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
