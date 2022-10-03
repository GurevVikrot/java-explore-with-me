package ru.explore.with.me.service.compilation;

import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    String deleteCompilation(long compId);

    String addEventToCompilation(long compId, long eventId);

    String removeEventFromCompilation(long compId, long eventId);

    String pinCompilation(long compId, boolean pin);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compId);
}
