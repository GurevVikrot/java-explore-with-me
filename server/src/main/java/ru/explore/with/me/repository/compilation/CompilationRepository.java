package ru.explore.with.me.repository.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.compilation.Compilation;

import java.util.List;

/**
 * Jpa репозиторий подборок
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    /**
     * Возвращает список подборок из БД, в зависимости от запрашиваемого поля pinned
     *
     * @param pinned закрепление подборки true|false
     * @param page   Параметр пагинации
     * @return List<Compilation> Список подборок
     */
    List<Compilation> findAllByPinnedIs(Boolean pinned, Pageable page);
}
