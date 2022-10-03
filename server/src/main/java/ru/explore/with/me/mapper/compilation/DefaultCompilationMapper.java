package ru.explore.with.me.mapper.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;
import ru.explore.with.me.mapper.event.EventMapper;
import ru.explore.with.me.model.compilation.Compilation;

import java.util.stream.Collectors;

@Component
public class DefaultCompilationMapper implements CompilationMapper {
    private final EventMapper eventMapper;

    @Autowired
    public DefaultCompilationMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    /**
     * Преобразует NewCompilationDto в Compilation.
     * Объекс Compilation возвращается с id == null и events == null.
     * events требуют заполнения в классе сервиса.
     * @param newCompilationDto Dto объект новой подборки
     * @return Compilation Объект подборки
     */
    @Override
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(null,
                newCompilationDto.getTitle(),
                newCompilationDto.isPinned(),
                null);
    }

    /**
     * Преобразует Compilation в CompilationDto
     * @param compilation Объект Подборки
     * @return CompilationDto Dto объект подборки
     */
    @Override
    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                compilation.getEvents().stream()
                        .map(eventMapper::toShortEventDto)
                        .collect(Collectors.toList()));
    }
}
