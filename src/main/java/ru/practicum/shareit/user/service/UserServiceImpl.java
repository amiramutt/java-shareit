package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<CreateUserDto> getAllUsers() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public CreateUserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    public CreateUserDto addUser(CreateUserDto createUserDto) {
        userStorage.getUserByEmail(createUserDto.getEmail()).ifPresent(u -> {
            throw new InternalServerException("Email " + createUserDto.getEmail() + " уже используется другим пользователем.");
        });
        User user = UserMapper.toUser(createUserDto);
        User savedUser = userStorage.addUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public CreateUserDto updateUser(Long userId, UpdateUserDto newUserDto) {
        User existingUser = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден для обновления."));

        if (newUserDto.getEmail() != null && !newUserDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userStorage.getUserByEmail(newUserDto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new InternalServerException("Email " + newUserDto.getEmail() + " уже используется другим пользователем.");
                }
            });
        }

        User userToUpdate = UserMapper.toUser(newUserDto);
        userToUpdate.setId(userId);

        User updatedUser = userStorage.updateUser(userToUpdate)
                .orElseThrow(() -> new NotFoundException("Не удалось обновить пользователя с ID " + userId));

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        userStorage.deleteUserById(userId);
    }
}
