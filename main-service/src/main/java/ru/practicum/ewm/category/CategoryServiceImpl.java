package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id = %d не найдена", catId));
        }
        if (!eventRepository.findByCategoryId(catId).isEmpty()) {
            throw new DataIntegrityViolationException(String.format("Категория с id = %d связана с одним из событий", catId));
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryInDto) {
        Category category = findCategoryById(catId);
        category.setName(newCategoryInDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        return CategoryMapper.toListOfCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Category category = findCategoryById(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    private Category findCategoryById(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id = %d не найдена", catId)));
    }
}