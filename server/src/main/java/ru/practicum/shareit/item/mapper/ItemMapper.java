package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    private ItemMapper() {
    }

    public static CreateItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return CreateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(CreateItemDto createItemDto, User owner) {
        if (createItemDto == null) {
            return null;
        }
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .available(createItemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        if (commentDto == null) {
            return null;
        }
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getDateCreated())
                .build();
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item, BookingDto lastBooking,
                                                    BookingDto nextBooking, List<CommentDto> comments) {
        ItemWithBookingDto dto = new ItemWithBookingDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }

    public static List<CreateItemDto> toListDto(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

}
