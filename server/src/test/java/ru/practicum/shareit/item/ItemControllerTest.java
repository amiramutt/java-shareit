package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private ItemWithBookingDto itemWithBookingDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto();
        createItemDto.setId(1L);
        createItemDto.setName("Item1");

        updateItemDto = new UpdateItemDto();
        updateItemDto.setId(1L);
        updateItemDto.setName("Updated Item");

        itemWithBookingDto = new ItemWithBookingDto();
        itemWithBookingDto.setId(1L);
        itemWithBookingDto.setName("Item1");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good item");
    }

    @Test
    void addItemSuccess() throws Exception {
        when(itemService.addItem(anyLong(), any(CreateItemDto.class))).thenReturn(createItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createItemDto.getName())));

        verify(itemService, times(1)).addItem(anyLong(), any(CreateItemDto.class));
    }

    @Test
    void updateItemSuccess() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(UpdateItemDto.class))).thenReturn(createItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createItemDto.getId()), Long.class));

        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    void getItemByIdSuccess() throws Exception {
        when(itemService.getItemById(anyLong())).thenReturn(itemWithBookingDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())));

        verify(itemService, times(1)).getItemById(anyLong());
    }

    @Test
    void getAllItemsByUserIdSuccess() throws Exception {
        when(itemService.getAllItemsByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(itemWithBookingDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Long.class));

        verify(itemService, times(1)).getAllItemsByOwnerId(anyLong());
    }

    @Test
    void getAvailableItemsByTextSuccess() throws Exception {
        when(itemService.getAvailableItemsByText(any(String.class)))
                .thenReturn(Collections.singletonList(createItemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(createItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(createItemDto.getName())));

        verify(itemService, times(1)).getAvailableItemsByText(any(String.class));
    }

    @Test
    void addCommentSuccess() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}
