package ru.explore.with.me.service.category;

import ru.explore.with.me.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    String deleteCategory(int catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(int catId) throws NoContentException;
}
