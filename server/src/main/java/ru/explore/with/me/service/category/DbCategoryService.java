package ru.explore.with.me.service.category;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.mapper.category.CategoryMapper;
import ru.explore.with.me.model.category.Category;
import ru.explore.with.me.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbCategoryService implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository repository;

    public DbCategoryService(CategoryMapper categoryMapper,
                             CategoryRepository repository) {
        this.categoryMapper = categoryMapper;
        this.repository = repository;
    }

    /**
     * Создание категории
     * @param categoryDto Dto объект категории
     * @return categoryDto
     */
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(repository.save(category));
    }

    /**
     * Обновление категории
     * @param categoryDto Dto объект категории
     * @return categoryDto
     */
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        if(!repository.existsById(categoryDto.getId())) {
            throw new ValidationException("Категория не найдена");
        }

        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(repository.save(category));
    }

    /**
     * Удаление категории
     * @param catId Id категории
     * @return String статус удаления
     */
    @Override
    public String deleteCategory(int catId) {
        if(repository.existsById(catId)) {
            repository.deleteById(catId);
            return "Категория удалена";
        }

        throw new NotFoundException("Категория не найдена");
    }

    /**
     * Получение всех категорий.
     * @param from Сколько строк нужно пропустить
     * @param size Размер страницы
     * @return List CategoryDto Список категорий
     */
    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from/size,size);
        return repository.findAll(page).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(int catId) {
        return categoryMapper.toCategoryDto(
                repository.findById(catId).orElseThrow(
                        () -> new NotFoundException("Категория не найдена")));
    }
}
