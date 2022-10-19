package ru.explore.with.me.mapper.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.model.event.Event;

/**
 * Интерфейс маппера Event и его Dto
 */
public interface EventMapper {
    /**
     * Преобразование в краткую форму Dto
     *
     * @param event Объект события
     * @return EventShortDto
     */
    EventShortDto toShortEventDto(Event event);

    /**
     * Преобразование в полную форму Dto
     *
     * @param event Объект события
     * @return EventShortDto
     */
    EventFullDto toEventFullDto(Event event);

    /**
     * Преобразование Dto в Event для нового события
     *
     * @param requestEventDto Dto объект
     * @return Event
     */
    Event toNewEvent(RequestEventDto requestEventDto);
}
