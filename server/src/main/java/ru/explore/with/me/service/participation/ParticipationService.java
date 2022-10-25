package ru.explore.with.me.service.participation;

import ru.explore.with.me.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> getParticipantsOfEvent(long userId, long eventId);

    ParticipationRequestDto setParticipationStatus(long userId, long eventId, long reqId, boolean agree);

    List<ParticipationRequestDto> getUserParticipation(long userId);

    ParticipationRequestDto newParticipation(long userId, long eventId);

    ParticipationRequestDto cancelParticipation(long userId, long requestId);
}
