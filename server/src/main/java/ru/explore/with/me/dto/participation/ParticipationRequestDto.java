package ru.explore.with.me.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.util.ParticipantStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class ParticipationRequestDto {
    private long id;
    private long event;
    private long requester;
    private LocalDateTime created;
    private ParticipantStatus status;
}
