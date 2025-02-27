package ru.practicum.ewm.comments;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import java.util.List;

public interface CommentService {

    CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentByAuthor(long userId, long commentId, NewCommentDto updateComment);

    CommentDto cancelComment(long userId, long commentId);

    List<CommentDto> getAllCommentsByUserId(long userId);

    void deleteCommentByAdmin(long commentId);

    List<CommentDto> getAdminCommentsWithParams(String text, int from, int size);

    List<CommentDto> getPublicCommentsWithParams(long eventId, int from, int size);
}