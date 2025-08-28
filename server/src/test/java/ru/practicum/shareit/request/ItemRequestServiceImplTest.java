package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requester;
    private ItemRequestCreateDto createDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requester = new User(1L, "User", "user@mail.com");

        createDto = new ItemRequestCreateDto("desc1");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("desc1");
        itemRequest.setRequester(requester);
        itemRequest.setDateCreated(LocalDateTime.now());
    }

    @Test
    void createItemRequestSuccess() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(requester.getId(), createDto);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequestUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(99L, createDto));
    }

    @Test
    void getItemRequestsByUserIdSuccess() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByDateCreatedDesc(requester.getId()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getItemRequestsByUserId(requester.getId());

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        verify(itemRequestRepository).findAllByRequesterIdOrderByDateCreatedDesc(requester.getId());
    }

    @Test
    void getItemRequestsByUserIdEmpty() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByDateCreatedDesc(requester.getId()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByUserId(requester.getId()));
    }

    @Test
    void findAllByNotRequesterIdSortedSuccess() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByDateCreatedDesc(requester.getId()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.findAllByNotRequesterIdSorted(requester.getId());

        assertEquals(1, result.size());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        verify(itemRequestRepository).findAllByRequesterIdNotOrderByDateCreatedDesc(requester.getId());
    }

    @Test
    void findAllByNotRequesterIdSortedEmpty() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByDateCreatedDesc(requester.getId()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllByNotRequesterIdSorted(requester.getId()));
    }

    @Test
    void getItemRequestByIdSuccess() {
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(itemRequest.getId());

        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        verify(itemRequestRepository).findById(itemRequest.getId());
    }

    @Test
    void getItemRequestByIdNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(99L));
    }
}
