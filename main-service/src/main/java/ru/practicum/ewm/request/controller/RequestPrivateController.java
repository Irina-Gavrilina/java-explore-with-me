package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequestsByUserId(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получении информации о заявках пользователя c id = {} на участие в чужих событиях", userId);
        return requestService.getAllRequestsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") long userId,
                                                 @RequestParam(value = "eventId") long eventId) {
        log.info("Получен запрос POST на создание запроса на участие в событии c id = {} от пользователя с id = {}",
                eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") long userId,
                                                 @PathVariable("requestId") long requestId) {
        log.info("Поступил запрос PATCH от пользователя с id = {} на отмену своего запроса на участие в событии с id = {},", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}