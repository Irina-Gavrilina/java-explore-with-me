package ru.practicum.ewm.comments.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.CommentService;
import ru.practicum.ewm.comments.dto.CommentDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getPublicCommentsWithParams(@PathVariable("eventId") long eventId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил запрос GET (CommentPublicController) на получение информации обо всех комментариях к " +
                "событию с id = {}, подходящих под переданные условия: from = {}, size = {}", eventId, from, size);
        return commentService.getPublicCommentsWithParams(eventId, from, size);
    }
}