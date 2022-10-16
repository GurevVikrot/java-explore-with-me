package ru.explore.with.me.mapper.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.explore.with.me.client.event.EventClient;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.mapper.category.CategoryMapper;
import ru.explore.with.me.mapper.user.UserMapper;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DefaultEventMapper implements EventMapper{
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final EventClient eventClient;

    @Autowired
    public DefaultEventMapper(UserMapper userMapper, CategoryMapper categoryMapper, EventClient eventClient) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.eventClient = eventClient;
    }

    /**
     * Преобразование Event в EventShortDto
     * Заполнение поля views требуется в сервисах, использующих класс.
     * @param event объект события
     * @return EventShortDto
     */
    @Override
    public EventShortDto toShortEventDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserShortDto(event.getCreator()),
                event.isPaid(),
                event.getParticipations().size(),
                event.getEventDate(),
                getEventViews(event.getId(), event.getPublishedOn()));
    }

    @Override
    public Event toEvent(EventFullDto eventFullDto) {
        return new Event(
                eventFullDto.getId() == 0? null : eventFullDto.getId(),
                eventFullDto.getTitle(),
                eventFullDto.getAnnotation(),
                eventFullDto.getDescription(),
                null,
                eventFullDto.getEventDate(),
                eventFullDto.isPaid(),
                eventFullDto.getCost(),
                null,
                null,
                eventFullDto.getParticipantLimit(),
                eventFullDto.isRequestModeration(),
                null,
                null,
                null);
    }

    @Override
    public EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                userMapper.toUserShortDto(event.getCreator()),
                event.getEventDate(),
                event.getCreated(),
                event.getPublishedOn(),
                categoryMapper.toCategoryDto(event.getCategory()),
                event.isPaid(),
                event.getCost(),
                event.getParticipations().size(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                event.getStatus(),
                getEventViews(event.getId(), event.getPublishedOn()));
    }

    @Override
    public Event toNewEvent(RequestEventDto requestEventDto) {
        return new Event(null,
                requestEventDto.getTitle().trim(),
                requestEventDto.getAnnotation().trim(),
                requestEventDto.getDescription().trim(),
                null,
                requestEventDto.getEventDate(),
                requestEventDto.getPaid() != null && requestEventDto.getPaid(),
                0,
                null,
                List.of(),
                requestEventDto.getParticipantLimit(),
                requestEventDto.getRequestModeration(),
                EventStatus.PENDING,
                LocalDateTime.now(),
                null);
    }

    private int getEventViews(long eventId, LocalDateTime start) {
        if (start == null) {
            return 0;
        }
        return eventClient.getEventStatistic(eventId, start, LocalDateTime.now()).getHits();
    }
}
