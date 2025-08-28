package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class UserTest {

    @Autowired
    private JacksonTester<User> json;

    @Test
    void serializeAndDeserialize() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("user1");
        user.setEmail("user1@mail.com");

        String content = json.write(user).getJson();
        assertThat(content).contains("\"id\":1");
        assertThat(content).contains("\"name\":\"user1\"");
        assertThat(content).contains("\"email\":\"user1@mail.com\"");

        String jsonStr = "{\"id\":2,\"name\":\"user2\",\"email\":\"user2@mail.com\"}";
        User parsed = json.parseObject(jsonStr);
        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getName()).isEqualTo("user2");
        assertThat(parsed.getEmail()).isEqualTo("user2@mail.com");
    }
}


