package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.practicum.ewm.event.dto.EventShortDto;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEventsWithParams(@RequestParam(required = false) String text,
                                                         @RequestParam(required = false) List<Integer> categories,
                                                         @RequestParam(required = false) Boolean paid,
                                                         @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                         @RequestParam(required = false) String sort,
                                                         @RequestParam(required = false, defaultValue = "0")
                                                         @PositiveOrZero int from,
                                                         @RequestParam(required = false, defaultValue = "10")
                                                         @Positive int size,
                                                         HttpServletRequest request) {
        log.info("Поступил запрос GET (EventPublicController) на получение списка событий с учетом параметров: text = {}, " +
                "categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getPublicEventsWithParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublicEventById(@PathVariable("eventId") long eventId, HttpServletRequest request) {
        log.info("Поступил запрос GET (EventPublicController) на получения полной информации о событии с  id= {}",
                eventId);
        return eventService.getPublicEventById(eventId, request);
    }
}