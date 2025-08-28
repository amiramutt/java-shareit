package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void saveAndFindByIdSuccess() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "item1", "desc1", true, owner, null));

        Booking booking = new Booking(null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item, booker, BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);

        Booking found = bookingRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getItem().getName()).isEqualTo("item1");
        assertThat(found.getBooker().getEmail()).isEqualTo("booker@mail.com");
    }

    @Test
    void findByBookerIdOrderByStartDateDescSuccess() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "item2", "desc2", true, owner, null));

        Booking booking1 = new Booking(null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item, booker, BookingStatus.WAITING);

        Booking booking2 = new Booking(null,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                item, booker, BookingStatus.APPROVED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> found = bookingRepository.findByBookerIdOrderByStartDateDesc(booker.getId());

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getStartDate()).isAfter(found.get(1).getStartDate());
    }

    @Test
    void findByItemOwnerIdOrderByStartDateDescSuccess() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User booker = userRepository.save(new User(null, "booker", "booker@mail.com"));
        Item item = itemRepository.save(new Item(null, "item3", "desc3", true, owner, null));

        Booking booking = new Booking(null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item, booker, BookingStatus.WAITING);

        bookingRepository.save(booking);

        List<Booking> found = bookingRepository.findByItemOwnerIdOrderByStartDateDesc(owner.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getItem().getOwner().getId()).isEqualTo(owner.getId());
    }
}
