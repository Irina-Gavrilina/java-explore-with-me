package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос POST (CompilationAdminController) на создание подборки событий {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable("compId") long compId,
                                            @RequestBody @Valid UpdateCompilationRequest request) {
        log.info("Поступил запрос PATCH (CompilationAdminController) на изменение данных подборки событий c id = {}", compId);
        return compilationService.updateCompilation(compId, request);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Поступил запрос DELETE (CompilationAdminController) на удаление подборки событий с id = {}", compId);
        compilationService.deleteCompilation(compId);
    }
}