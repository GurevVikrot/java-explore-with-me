package ru.explore.with.me.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.explore.with.me.dto.event.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Dto объект подборки событий. Используется для ответа
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
