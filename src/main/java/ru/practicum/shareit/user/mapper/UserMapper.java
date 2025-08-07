package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static CreateUserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return CreateUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(CreateUserDto createUserDto) {
        if (createUserDto == null) {
            return null;
        }
        return User.builder()
                .name(createUserDto.getName())
                .email(createUserDto.getEmail())
                .build();
    }

    public static User toUser(UpdateUserDto newUserDto) {
        if (newUserDto == null) {
            return null;
        }
        return User.builder()
                .id(newUserDto.getId())
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }
}
