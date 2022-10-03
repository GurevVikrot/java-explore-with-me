package ru.explore.with.me.mapper.category;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.model.category.Category;

@Component
public class DefaultCategoryMapper implements CategoryMapper {

    @Override
    public Category toCategory(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName(), null);
    }

    @Override
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
