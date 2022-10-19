package ru.explore.with.me.dto.compilation;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Dto объект для создания новой подборки событий. Заложена валидация.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@ToString
public class NewCompilationDto {
    @NotBlank
    private String title;
    private boolean pinned;
    private List<Long> events;
}
