package ru.explore.with.me.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.util.EventStatus;
import ru.explore.with.me.util.Location;

import java.time.LocalDateTime;

/**
 * Dto объект с подробной информацией о событии для ответа
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private long id;
    private String title;
    private String annotation;
    private String description;
    private UserShortDto initiator;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private CategoryDto category;
    private boolean paid;
    private int cost;
    private int confirmedRequests;
    private int participantLimit;
    private boolean requestModeration;
    private EventStatus state;
    private int views;
    private Location location;
}
