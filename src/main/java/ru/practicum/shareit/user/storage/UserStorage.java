package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    Optional<User> getUserById(Long userId);

    User addUser(User user);

    Optional<User> updateUser(User user);

    void deleteUserById(Long userId);

    Optional<User> getUserByEmail(String email);
}
