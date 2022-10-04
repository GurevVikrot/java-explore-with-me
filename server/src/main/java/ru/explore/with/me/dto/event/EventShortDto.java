package ru.explore.with.me.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventShortDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private boolean paid;
    private int confirmedRequests;
    private LocalDateTime eventDate;
    private int views;
}
