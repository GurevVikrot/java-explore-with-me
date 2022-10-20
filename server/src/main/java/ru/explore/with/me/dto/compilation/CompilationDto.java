package ru.explore.with.me.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.dto.event.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Dto объект подборки событий. Используется для ответа
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
