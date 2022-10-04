package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public EventFullDto createEvent(@PathVariable @Positive long userId,
                                    @RequestBody @Valid RequestEventDto requestEventDto) {
        log.info("Пользователь id = {}. Создание события: {}", userId, requestEventDto);
        return eventService.createEvent(userId, requestEventDto);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable @Positive long userId,
                                    @RequestBody @Valid RequestEventDto requestEventDto) {
        log.info("Пользователь id = {}. Обновление события: {}", userId, requestEventDto);
        return eventService.updateEvent(userId, requestEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getCreatorEvents(@PathVariable @Positive long userId,
                                             @RequestParam(required = false, defaultValue = "0") int from,
                                             @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получение всех событий созданных пользователем Id = {}\n" +
                "Параметры пагинации: from = {} size = {}", userId, from, size);
        return eventService.getUserEvents(userId,from,size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByCreator(@PathVariable @Positive long userId,
                                          @PathVariable @Positive long eventId) {
        log.info("Получение события id = {} создателя id = {}", eventId, userId);
        return eventService.getEventByCreator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEventByCreator(@PathVariable @Positive long userId,
                                          @PathVariable @Positive long eventId) {
        log.info("Отмена события id = {} создателя id = {}", eventId, userId);
        return eventService.cancelEventByCreator(userId, eventId);
}
