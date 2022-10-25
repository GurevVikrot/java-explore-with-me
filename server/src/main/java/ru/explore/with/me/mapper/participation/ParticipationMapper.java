package ru.explore.with.me.mapper.participation;

import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.model.participation.Participation;

/**
 * Интерфейс маппинга участия в событии Participation в его Dto
 */
public interface ParticipationMapper {
    /**
     * Преобразование Participation в ParticipationRequestDto
     *
     * @param participation Объект заявки на участие в событии
     * @return ParticipationRequestDto
     */
    ParticipationRequestDto toParticipationDto(Participation participation);
}
