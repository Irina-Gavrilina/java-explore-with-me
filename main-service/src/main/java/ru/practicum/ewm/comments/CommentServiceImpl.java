package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflict;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // -------- PRIVATE --------

    @Override
    @Transactional
    public CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = findUserById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ValidationConflict("Добавлять комментарии можно только к опубликованным событиям");
        }

        Comment comment = CommentMapper.toComment(newCommentDto, event, user);
        comment.setStatus(CommentStatus.PENDING);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAuthor(long userId, long commentId, NewCommentDto updateComment) {
        checkUserExists(userId);
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с id = %d не найден", commentId)));

        if (LocalDateTime.now().isAfter(comment.getCreatedOn().plusHours(1))) {
            throw new ValidationConflict("Редактирование комментария невозможно, " +
                    "так как с момента его создания прошло более часа");
        }

        comment.setText(updateComment.getText());
        comment.setStatus(CommentStatus.PENDING);
        comment.setLastUpdatedOn(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto cancelComment(long userId, long commentId) {
        checkUserExists(userId);
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с id = %d не найден", commentId)));

        if (comment.getStatus().equals(CommentStatus.CANCELED)
                || comment.getStatus().equals(CommentStatus.REJECTED)) {
            throw new ValidationConflict("Данный комментарий уже отменен/отклонен");
        }

        comment.setStatus(CommentStatus.CANCELED);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllCommentsByUserId(long userId) {
        checkUserExists(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId);
        return CommentMapper.toListOfCommentDto(comments);
    }

    // -------- PUBLIC --------

    @Override
    public List<CommentDto> getPublicCommentsWithParams(long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return CommentMapper.toListOfCommentDto(comments);
    }

    // -------- ADMIN --------

    @Override
    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format("Комментарий с id = %d не найден", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAdminCommentsWithParams(String text, int from, int size) {
        String searchText = (text != null) ? text.toLowerCase() : null;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllForAdminByParams(searchText, pageable);
        return CommentMapper.toListOfCommentDto(comments);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }
}