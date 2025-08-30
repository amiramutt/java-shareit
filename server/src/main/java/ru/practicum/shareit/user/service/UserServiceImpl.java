package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<CreateUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public CreateUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    public CreateUserDto addUser(CreateUserDto createUserDto) {
        userRepository.findByEmailIgnoreCase(createUserDto.getEmail()).ifPresent(u -> {
            throw new InternalServerException("Email " + createUserDto.getEmail() + " уже используется другим пользователем.");
        });
        User user = UserMapper.toUser(createUserDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public CreateUserDto updateUser(Long userId, UpdateUserDto newUserDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден для обновления."));

        if (newUserDto.getName() != null && !newUserDto.getName().isBlank()) {
            existingUser.setName(newUserDto.getName());
        }

        if (newUserDto.getEmail() != null && !newUserDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userRepository.findByEmailIgnoreCase(newUserDto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new InternalServerException("Email " + newUserDto.getEmail() + " уже используется другим пользователем.");
                }
            });
            existingUser.setEmail(newUserDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        userRepository.deleteById(userId);
    }
}
