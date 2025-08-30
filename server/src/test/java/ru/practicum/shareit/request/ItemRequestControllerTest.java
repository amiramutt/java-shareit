package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateUserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequestSuccess() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("desc1");
        CreateUserDto userDto = new CreateUserDto(999L, "Mock User", "mock@mail.com");
        ItemRequestDto responseDto = new ItemRequestDto(
                1L,
                "desc1",
                userDto,
                LocalDateTime.now(),
                List.of()
        );

        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("desc1")))
                .andExpect(jsonPath("$.requester.id", is(999)));
    }

    @Test
    void getItemRequestsByUserIdSuccess() throws Exception {
        CreateUserDto userDto = new CreateUserDto(999L, "Mock User", "mock@mail.com");

        ItemRequestDto dto = new ItemRequestDto(
                2L,
                "desc2",
                userDto,
                LocalDateTime.now(),
                List.of()
        );

        when(itemRequestService.getItemRequestsByUserId(999L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].description", is("desc2")));
    }

    @Test
    void getItemRequestsByNotUserId() throws Exception {
        CreateUserDto userDto = new CreateUserDto(999L, "Mock User", "mock@mail.com");

        ItemRequestDto dto = new ItemRequestDto(
                3L,
                "desc3",
                userDto,
                LocalDateTime.of(2025, 1, 3, 14, 0),
                List.of()
        );

        when(itemRequestService.findAllByNotRequesterIdSorted(999L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].description", is("desc3")));
    }

    @Test
    void getItemRequestByIdSuccess() throws Exception {
        CreateUserDto userDto = new CreateUserDto(999L, "Mock User", "mock@mail.com");

        ItemRequestDto dto = new ItemRequestDto(
                4L,
                "desc4",
                userDto,
                LocalDateTime.of(2025, 1, 4, 15, 0),
                List.of()
        );

        when(itemRequestService.getItemRequestById(4L))
                .thenReturn(dto);

        mockMvc.perform(get("/requests/4")
                        .header("X-Sharer-User-Id", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.description", is("desc4")));
    }
}

