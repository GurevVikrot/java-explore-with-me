package ru.explore.with.me.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.compilation.CompilationEvents;
import ru.explore.with.me.model.compilation.CompilationEventsId;

public interface CompilationEventsRepository extends JpaRepository<CompilationEvents, CompilationEventsId> {
    void deleteAllByCompilationId(long compilationId);
}
