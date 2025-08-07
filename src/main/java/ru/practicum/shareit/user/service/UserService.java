package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;

public interface UserService {
    List<CreateUserDto> getAllUsers();

    CreateUserDto getUserById(Long userId);

    CreateUserDto addUser(CreateUserDto createUserDto);

    CreateUserDto updateUser(Long userId, UpdateUserDto userDto);

    void deleteUser(Long userId);
}
