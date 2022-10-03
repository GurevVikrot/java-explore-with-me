package ru.explore.with.me.dto.event;

import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private List<CategoryDto> category;
    private UserDto initiator;
    private boolean paid;
    private int confirmedRequests;
    private LocalDateTime eventDate;
    private int views;
}
