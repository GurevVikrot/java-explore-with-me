package ru.explore.with.me.service.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.util.EventSort;
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

    List<EventShortDto> getPublicEvents(String text,
                                        List<Integer> categories,
                                        boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        boolean onlyAvailable,
                                        EventSort sort,
                                        int from,
                                        int size,
                                        HttpServletRequest request);

    EventFullDto editEventByAdmin(RequestEventDto eventDto, long eventId);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);

    List<EventShortDto> getUserEventsToSub(long subId, long userId);
}
