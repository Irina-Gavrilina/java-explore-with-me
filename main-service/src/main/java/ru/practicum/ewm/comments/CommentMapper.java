package ru.practicum.ewm.comments;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .status(comment.getStatus())
                .build();
    }

    public static List<CommentDto> toListOfCommentDto(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    public static Comment toComment(NewCommentDto newCommentDto, Event event, User user) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(user)
                .createdOn(LocalDateTime.now())
                .lastUpdatedOn(null)
                .build();
    }
}