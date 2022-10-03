package ru.explore.with.me.service.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.NewEventDto;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getUserEvents(long userId, int from, int size);
    List<EventFullDto> getEventsToAdmin(List<Long> users, List<EventStatus> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto getEvent(long id);

    EventFullDto createEvent(long userId, NewEventDto newEventDto);
}
