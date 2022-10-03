package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.NewEventDto;
import ru.explore.with.me.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    private final EventService eventService;

    @Autowired
    public UserController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Пользователь id = {}. Создание события: {}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }
}
