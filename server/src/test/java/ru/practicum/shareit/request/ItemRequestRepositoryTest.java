package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByIdSuccess() {
        User requester = userRepository.save(new User(null, "Requester", "req@mail.com"));
        ItemRequest request = new ItemRequest();
        request.setDescription("desc1");
        request.setRequester(requester);
        request.setDateCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(request);

        Optional<ItemRequest> found = itemRequestRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("desc1");
        assertThat(found.get().getRequester().getEmail()).isEqualTo("req@mail.com");
    }

    @Test
    void findAllByRequesterIdOrderByDateCreatedDesc() {
        User requester = userRepository.save(new User(null, "Requester", "req@mail.com"));
        ItemRequest request = new ItemRequest();
        request.setDescription("desc1");
        request.setRequester(requester);
        request.setDateCreated(LocalDateTime.now().minusDays(1));

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("desc2");
        request2.setRequester(requester);
        request2.setDateCreated(LocalDateTime.now());

        itemRequestRepository.save(request);
        itemRequestRepository.save(request2);

        List<ItemRequest> found = itemRequestRepository.findAllByRequesterIdOrderByDateCreatedDesc(requester.getId());

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getDescription()).isEqualTo("desc2");
    }

    @Test
    void findAllByRequesterIdNotOrderByDateCreatedDesc() {
        User requester1 = userRepository.save(new User(null, "Requester1", "req1@mail.com"));
        User requester2 = userRepository.save(new User(null, "Requester2", "req2@mail.com"));

        ItemRequest request = new ItemRequest();
        request.setDescription("desc1");
        request.setRequester(requester1);
        request.setDateCreated(LocalDateTime.now().minusDays(1));

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("desc2");
        request2.setRequester(requester2);
        request2.setDateCreated(LocalDateTime.now());

        itemRequestRepository.save(request);
        itemRequestRepository.save(request2);

        List<ItemRequest> found = itemRequestRepository.findAllByRequesterIdNotOrderByDateCreatedDesc(requester1.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getRequester().getEmail()).isEqualTo("req2@mail.com");
    }
}
