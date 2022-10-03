package ru.explore.with.me.mapper.category;

import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.model.category.Category;

public interface CategoryMapper {

    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);
}
