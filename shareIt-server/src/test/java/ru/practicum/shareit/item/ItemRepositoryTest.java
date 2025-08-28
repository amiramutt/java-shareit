package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByOwnerIdSuccess() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));
        User another = userRepository.save(new User(null, "another", "another@mail.com"));

        Item item1 = itemRepository.save(new Item(null, "item1", "desc1", true, owner, null));
        Item item2 = itemRepository.save(new Item(null, "item2", "desc2", true, owner, null));
        itemRepository.save(new Item(null, "item3", "desc3", true, another, null));

        List<Item> ownerItems = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(ownerItems).hasSize(2);
        assertThat(ownerItems).extracting(Item::getName).containsExactlyInAnyOrder(item1.getName(), item2.getName());
    }

    @Test
    void getAvailableItemsByTextSuccess() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));

        itemRepository.save(new Item(null, "item111", "desc 111", true, owner, null));
        itemRepository.save(new Item(null, "item222", "desc 222", true, owner, null));
        itemRepository.save(new Item(null, "item333", "desc 333", false, owner, null)); // недоступен

        List<Item> foundByName = itemRepository.getAvailableItemsByText("Item1");
        List<Item> foundByDescription = itemRepository.getAvailableItemsByText("desc 222");

        assertThat(foundByName).hasSize(1);
        assertThat(foundByName.get(0).getName()).isEqualTo("item111");

        assertThat(foundByDescription).hasSize(1);
        assertThat(foundByDescription.get(0).getName()).isEqualTo("item222");
    }

    @Test
    void getAvailableItemsByTextNotFound() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.com"));

        itemRepository.save(new Item(null, "item111", "desc 111", true, owner, null));

        List<Item> notFound = itemRepository.getAvailableItemsByText("desc 222");

        assertThat(notFound).isEmpty();
    }
}
