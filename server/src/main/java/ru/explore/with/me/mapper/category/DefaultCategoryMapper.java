package ru.explore.with.me.mapper.category;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.model.category.Category;

/**
 * Реализация интерфейса мапинга категорий для работы с БД
 */
@Component
public class DefaultCategoryMapper implements CategoryMapper {

    @Override
    public Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId() == 0 ? null : categoryDto.getId(),
                categoryDto.getName().trim(),
                null);
    }

    @Override
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName());
    }
}
