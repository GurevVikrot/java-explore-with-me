package ru.explore.with.me.service.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.util.EventStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventShortDto> getUserEvents(long userId, int from, int size);
    List<EventFullDto> getEventsToAdmin(List<Long> users, List<EventStatus> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto getEvent(long id, HttpServletRequest request);

    EventFullDto createEvent(long userId, RequestEventDto requestEventDto);

    EventFullDto updateEvent(long userId, RequestEventDto requestEventDto);

    EventFullDto getEventByCreator(long userId, long eventId);

    EventFullDto cancelEventByCreator(long userId, long eventId);

    List<EventShortDto> getPublicEvents(String text, Set<Integer> categories, boolean paid, String rangeStart, String rangeEnd, boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);
}
