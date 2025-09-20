package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Test User", "test@mail.com");
    }

    @Test
    void getExistingUserReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CreateUserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getNotExistingUserThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }
}

