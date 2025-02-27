package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAdminEventsWithParams(@RequestParam(required = false) List<Integer> users,
                                                       @RequestParam(required = false) List<String> states,
                                                       @RequestParam(required = false) List<Integer> categories,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                                       @RequestParam(required = false, defaultValue = "0")
                                                       @PositiveOrZero int from,
                                                       @RequestParam(required = false, defaultValue = "10")
                                                       @Positive int size) {
       log.info("Поступил запрос GET (EventAdminController) на получение информации обо всех событиях, " +
                        "подходящих под переданные условия: users = {}, states = {}, categories = {}, rangeStart = {}, " +
                       "rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAdminEventsWithParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable("eventId") long eventId,
                                           @Valid @RequestBody(required = false) UpdateEventAdminRequest request) {
        log.info("Получен запрос PATCH (EventAdminController) на изменение данных события c id = {}", eventId);
        return eventService.updateEventByAdmin(eventId, request);
    }
}