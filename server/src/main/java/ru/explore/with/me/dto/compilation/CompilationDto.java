package ru.explore.with.me.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.explore.with.me.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
