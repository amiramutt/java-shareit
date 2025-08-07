package ru.practicum.shareit.user.storage;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(currentId.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateUser(User userToUpdate) {
        if (userToUpdate.getId() == null || !users.containsKey(userToUpdate.getId())) {
            return Optional.empty();
        }
        User existingUser = users.get(userToUpdate.getId());

        if (userToUpdate.getName() != null && !userToUpdate.getName().isBlank()) {
            existingUser.setName(userToUpdate.getName());
        }
        if (userToUpdate.getEmail() != null && !userToUpdate.getEmail().isBlank()) {
            String newEmail = userToUpdate.getEmail();
            if (!newEmail.equals(existingUser.getEmail()) && users.values().stream().anyMatch(u -> u.getEmail().equals(newEmail))) {
                throw new InternalServerException("Email " + newEmail + " уже используется другим пользователем.");
            }
            existingUser.setEmail(newEmail);
        }

        users.put(existingUser.getId(), existingUser);
        return Optional.of(existingUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
