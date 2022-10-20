package ru.explore.with.me.mapper.compilation;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;
import ru.explore.with.me.model.compilation.Compilation;

/**
 * Интерфейс маппинга Compilation и его Dto
 */
@Component
public interface CompilationMapper {
    /**
     * Преобразование NewCompilationDto в Compilation.
     *
     * @param compilationDto Dto объект новой подборки
     * @return Compilation Объект подборки
     */
    Compilation toCompilation(NewCompilationDto compilationDto);

    /**
     * Преобразует Compilation в CompilationDto
     *
     * @param compilation Объект Подборки
     * @return CompilationDto Dto объект подборки
     */
    CompilationDto toCompilationDto(Compilation compilation);
}
