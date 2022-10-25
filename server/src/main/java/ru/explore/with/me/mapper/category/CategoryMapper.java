package ru.explore.with.me.mapper.category;

import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.model.category.Category;

/**
 * Интерфейс маппинга Category и его Dto
 */
public interface CategoryMapper {
    /**
     * Преобразование CategoryDto в Category
     *
     * @param categoryDto Dto объект категории
     * @return Category
     */
    Category toCategory(CategoryDto categoryDto);

    /**
     * Преобразование Category в CategoryDto
     *
     * @param category объект категории
     * @return CategoryDto
     */
    CategoryDto toCategoryDto(Category category);
}
