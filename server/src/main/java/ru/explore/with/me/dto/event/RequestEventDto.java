package ru.explore.with.me.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.explore.with.me.util.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Dto класс события с валидацией полей. Используется при создании новых событий и обновлении.
 * В Случае обновления необходимо отдельно валидировать поле eventId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    //    @Valid без валидации из-за тестов, в которых широта и долгота не связаны с реальным миром
    private Location location;
}
