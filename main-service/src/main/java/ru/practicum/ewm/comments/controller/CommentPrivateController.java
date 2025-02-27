package ru.practicum.ewm.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.CommentService;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("userId") long userId,
                                    @RequestParam(value = "eventId") long eventId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен запрос POST (CommentPrivateController) от пользователя с id = {} на создание нового " +
                "комментария {} к событию с id = {}", userId, newCommentDto, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable("userId") long userId,
                                    @PathVariable("commentId") long commentId,
                                    @RequestBody @Valid NewCommentDto updateComment) {
        log.info("Поступил запрос PATCH (CommentPrivateController) от пользователя с id = {} на изменение данных " +
                "комментария c id = {}", userId, commentId);
        return commentService.updateCommentByAuthor(userId, commentId, updateComment);
    }

    @PatchMapping("/{commentId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto cancelComment(@PathVariable("userId") long userId,
                                    @PathVariable("commentId") long commentId) {
        log.info("Поступил запрос PATCH (CommentPrivateController) от пользователя с id = {} на отмену своего " +
                        "комментария с id = {}", userId, commentId);
        return commentService.cancelComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsByUserId(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получении информации о всех комментариях пользователя c id = {}", userId);
        return commentService.getAllCommentsByUserId(userId);
    }
}