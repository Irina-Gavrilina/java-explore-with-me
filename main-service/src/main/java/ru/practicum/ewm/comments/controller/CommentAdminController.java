package ru.practicum.ewm.comments.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.CommentService;
import ru.practicum.ewm.comments.dto.CommentDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") long commentId) {
        log.info("Получен запрос DELETE (CommentAdminController) на удаление комментария c id = {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/search")
    public List<CommentDto> getAdminCommentsWithParams(@RequestParam(value = "text", required = false) String text,
                                                       @RequestParam(value = "from", defaultValue = "0")
                                                       @PositiveOrZero int from,
                                                       @RequestParam(value = "size", defaultValue = "10")
                                                       @Positive int size) {
        log.info("Поступил запрос GET (CommentAdminController) на получение информации обо всех комментариях, " +
                "подходящих под переданные условия: text = {}, from = {}, size = {}", text, from, size);
        return commentService.getAdminCommentsWithParams(text, from, size);
    }
}