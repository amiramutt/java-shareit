package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestTest {

    @Autowired
    private JacksonTester<ItemRequest> json;

    @Test
    void serializeAndDeserialize() throws Exception {
        User requester = new User();
        requester.setId(7L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("desc1");
        request.setRequester(requester);
        request.setDateCreated(LocalDateTime.of(2026, 2, 1, 14, 0));

        String content = json.write(request).getJson();
        assertThat(content).contains("\"id\":1");
        assertThat(content).contains("\"description\":\"desc1\"");
        assertThat(content).contains("2026-02-01T14:00:00");

        String jsonStr = "{\"id\":2,\"description\":\"desc2\",\"dateCreated\":\"2026-02-05T09:00:00\"}";
        ItemRequest parsed = json.parseObject(jsonStr);
        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getDescription()).isEqualTo("desc2");
        assertThat(parsed.getDateCreated()).isEqualTo(LocalDateTime.of(2026, 2, 5, 9, 0));
    }
}


