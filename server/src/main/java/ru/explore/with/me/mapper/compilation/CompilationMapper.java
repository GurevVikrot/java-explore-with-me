package ru.explore.with.me.mapper.compilation;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;
import ru.explore.with.me.model.compilation.Compilation;

@Component
public interface CompilationMapper {
    Compilation toCompilation(NewCompilationDto compilationDto);

    CompilationDto toCompilationDto(Compilation compilation);
}
