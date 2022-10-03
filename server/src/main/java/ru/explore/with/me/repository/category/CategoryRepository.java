package ru.explore.with.me.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
