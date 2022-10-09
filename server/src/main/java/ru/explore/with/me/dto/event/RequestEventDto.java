package ru.explore.with.me.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Dto класс события с валидацией полей. Используется при создании новых событий и обновлении.
 * В Случае создания необходимо отдельно валидировать поля eventId и requestModeration.
 * В Случае обновления необходимо отдельно валидировать поле eventId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class RequestEventDto {
    private long eventId;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank
    @Size(min = 10, max = 2000)
    private String annotation;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @Positive
    private int category;
    private LocalDateTime eventDate;
    private Boolean paid;
    @PositiveOrZero
    private int participantLimit;
    private Boolean requestModeration;
}
