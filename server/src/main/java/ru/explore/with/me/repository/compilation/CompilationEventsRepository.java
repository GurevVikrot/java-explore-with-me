package ru.explore.with.me.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.compilation.CompilationEvents;
import ru.explore.with.me.model.compilation.CompilationEventsId;

/**
 * Jpa репозиторий связующей таблицы подборок и событий
 */
public interface CompilationEventsRepository extends JpaRepository<CompilationEvents, CompilationEventsId> {
    void deleteAllByCompilationId(long compilationId);
}
