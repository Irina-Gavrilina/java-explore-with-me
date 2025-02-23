package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable("userId") long userId,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero int from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Positive int size) {
        log.info("Поступил запрос GET (EventPrivateController) на получение списка событий пользователя с id = {}",
                userId);
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос POST (EventPrivateController) на создание нового события {}", newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdAndOwnerId(@PathVariable("userId") long userId,
                                               @PathVariable("eventId") long eventId) {
        log.info("Поступил запрос GET (EventPrivateController) на получении информации о событии c id = {}, " +
                "добавленным пользователем с id = {}", eventId, userId);
        return eventService.getEventByIdAndOwnerId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByOwner(@PathVariable("userId") long userId,
                                           @PathVariable("eventId") long eventId,
                                           @RequestBody @Valid UpdateEventUserRequest request) {
        log.info("Получен запрос PATCH (EventPrivateController) на изменение данных события c id = {}", eventId);
        return eventService.updateEventByOwner(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllParticipationRequestsByEventOwnerId(@PathVariable("userId") long userId,
                                                                                   @PathVariable("eventId") long eventId) {
        log.info("Поступил запрос GET (EventPrivateController) на получении информации о всех запросах об участии " +
                        "в событии текущего пользователя с id= {}", userId);
        return eventService.getAllParticipationRequestsByEventOwnerId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeEventStateOfParticipationRequest(@PathVariable long userId,
                                                                                 @PathVariable long eventId,
                                                                                 @RequestBody
                                                                                 @Valid EventRequestStatusUpdateRequest request) {
        log.info("Получен запрос PATCH (EventPrivateController) на изменение статуса события c id = {}", eventId);
        return eventService.changeEventStateOfParticipationRequest(userId, eventId, request);
    }
}