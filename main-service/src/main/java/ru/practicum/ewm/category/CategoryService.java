package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, NewCategoryDto newCategoryInDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);
}