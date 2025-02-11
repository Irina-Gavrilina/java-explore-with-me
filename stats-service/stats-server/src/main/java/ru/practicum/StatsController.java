package ru.practicum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitResponse createHit(@Valid @RequestBody CreateHitRequest request) {
        log.info("Получен запрос POST на добавление запроса в статистику {}", request);
        return statService.createHit(request);
    }

    @GetMapping("/stats")
    @ResponseStatus
    public List<StatsResponse> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Поступил запрос GET на получение статистики по посещениям: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }
}