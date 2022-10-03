package ru.explore.with.me.mapper.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.NewEventDto;
import ru.explore.with.me.model.event.Event;

public interface EventMapper {
    EventShortDto toShortEventDto(Event event);

    Event toEvent(EventFullDto eventFullDto);

    EventFullDto toEventFullDto(Event event);

    Event toNewEvent(NewEventDto newEventDto);
}
