package ru.explore.with.me.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String title;
    private String annotation;
    private String description;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private CategoryDto category;
    private boolean paid;
    private int cost;
    private int confirmedRequest;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus state;
    private int views;
}
