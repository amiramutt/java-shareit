package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemTest {

    @Autowired
    private JacksonTester<Item> json;

    @Test
    void serializeAndDeserialize() throws Exception {
        User owner = new User();
        owner.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        item.setOwner(owner);

        String content = json.write(item).getJson();
        assertThat(content).contains("\"id\":1");
        assertThat(content).contains("\"name\":\"item1\"");
        assertThat(content).contains("\"available\":true");

        String jsonStr = "{\"id\":2,\"name\":\"item2\",\"description\":\"desc2\",\"available\":false}";
        Item parsed = json.parseObject(jsonStr);
        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getName()).isEqualTo("item2");
        assertThat(parsed.getAvailable()).isFalse();
    }
}


