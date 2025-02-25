package ru.practicum.ewm.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.dto.CategoryDto;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил запрос GET на получение списка категорий");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable("catId") long catId) {
        log.info("Поступил запрос GET на получение категории с id={}", catId);
        return categoryService.getCategoryById(catId);
    }
}