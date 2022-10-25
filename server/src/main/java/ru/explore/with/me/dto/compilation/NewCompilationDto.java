package ru.explore.with.me.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Dto объект для создания новой подборки событий. Заложена валидация.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class NewCompilationDto {
    @NotBlank
    private String title;
    private boolean pinned;
    private List<Long> events;
}
