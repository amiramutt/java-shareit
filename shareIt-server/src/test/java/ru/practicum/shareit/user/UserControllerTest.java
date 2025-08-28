package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.service.UserService;

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
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setId(1L);
        createUserDto.setName("Name");
        createUserDto.setEmail("name@mail.com");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updated@mail.com");
    }

    @Test
    void createUserSuccess() throws Exception {
        when(userService.addUser(any(CreateUserDto.class))).thenReturn(createUserDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(createUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createUserDto.getName())))
                .andExpect(jsonPath("$.email", is(createUserDto.getEmail())));

        verify(userService, times(1)).addUser(any(CreateUserDto.class));
    }

    @Test
    void updateUserSuccess() throws Exception {
        when(userService.updateUser(anyLong(), any(UpdateUserDto.class))).thenReturn(createUserDto);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto.getId()), Long.class));

        verify(userService, times(1)).updateUser(anyLong(), any(UpdateUserDto.class));
    }

    @Test
    void getUserByIdSuccess() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(createUserDto);

        mockMvc.perform(get("/users/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createUserDto.getName())))
                .andExpect(jsonPath("$.email", is(createUserDto.getEmail())));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void deleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(createUserDto));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(createUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(createUserDto.getName())))
                .andExpect(jsonPath("$[0].email", is(createUserDto.getEmail())));

        verify(userService, times(1)).getAllUsers();
    }
}
