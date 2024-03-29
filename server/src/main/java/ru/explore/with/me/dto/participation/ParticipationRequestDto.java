package ru.explore.with.me.dto.participation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.explore.with.me.util.ParticipantStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Dto класс участия в событии. Используется для ответа
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ParticipationRequestDto {
    private long id;
    private long event;
    private long requester;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private ParticipantStatus status;
}
