package ru.explore.with.me.mapper.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.NewEventDto;
import ru.explore.with.me.mapper.category.CategoryMapper;
import ru.explore.with.me.mapper.user.UserMapper;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultEventMapper implements EventMapper{
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Autowired
    public DefaultEventMapper(UserMapper userMapper, CategoryMapper categoryMapper) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public EventShortDto toShortEventDto(Event event) {
        return new EventShortDto();
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
                null
                );
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
                categoryMapper.toCategoryDto(event.getCategories()),
                event.isPaid(),
                event.getCost(),
                event.getParticipants().size(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                event.getStatus(),
                0
                );
    }

    @Override
    public Event toNewEvent(NewEventDto newEventDto) {
        return new Event(null,
                newEventDto.getTitle().trim(),
                newEventDto.getAnnotation().trim(),
                newEventDto.getDescription().trim(),
                null,
                newEventDto.getEventDate(),
                newEventDto.isPaid(),
                0,
                null,
                List.of(),
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration(),
                EventStatus.WAITING,
                LocalDateTime.now(),
                null);
    }
}
