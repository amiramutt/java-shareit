package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByIdSuccess() {
        User user = new User(null, "Test User", "test@mail.com");
        user = userRepository.save(user);

        Optional<User> found = userRepository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void findByEmailIgnoreCaseShouldReturnUser() {
        User user = new User(null, "Case User", "Case@mail.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailIgnoreCase("case@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Case User");
    }

    @Test
    void findAllShouldReturnAllUsers() {
        User user1 = new User(null, "User1", "u1@mail.com");
        User user2 = new User(null, "User2", "u2@mail.com");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("u1@mail.com", "u2@mail.com");
    }

    @Test
    void deleteByIdShouldRemoveUser() {
        User user = new User(null, "Delete User", "delete@mail.com");
        user = userRepository.save(user);

        userRepository.deleteById(user.getId());

        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found).isEmpty();
    }
}
