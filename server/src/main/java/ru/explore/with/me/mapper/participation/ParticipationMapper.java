package ru.explore.with.me.mapper.participation;

import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.model.participation.Participation;

public interface ParticipationMapper {
    ParticipationRequestDto toParticipationDto(Participation participation);
}
