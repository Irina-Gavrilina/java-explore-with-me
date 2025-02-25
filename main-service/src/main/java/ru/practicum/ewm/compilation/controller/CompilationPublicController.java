package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilationsByParams(@RequestParam(required = false) Boolean pinned,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил запрос GET (CompilationPublicController) на получение информации обо всех подборках " +
                        "событий по заданным параметрам: pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getCompilationsByParams(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable("compId") long compId) {
        log.info("Поступил запрос GET (CompilationPublicController) на получение подборки событий с id = {}", compId);
        return compilationService.getCompilationById(compId);
    }
}