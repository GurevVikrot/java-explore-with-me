package ru.explore.with.me.mapper.participation;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.model.participation.Participation;

/**
 * Реализация интерфейса маппинга участия в событии в его Dto
 */
@Component
public class DefaultParticipationMapper implements ParticipationMapper {
    @Override
    public ParticipationRequestDto toParticipationDto(Participation participation) {
        return new ParticipationRequestDto(
                participation.getId(),
                participation.getEvent().getId(),
                participation.getUser().getId(),
                participation.getCreated(),
                participation.getStatus());
    }
}
