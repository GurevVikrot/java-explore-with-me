package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.service.event.EventService;
import ru.explore.with.me.service.participation.ParticipationService;
import ru.explore.with.me.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Контроллер для авторизованных пользователей
 */
@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    private final EventService eventService;
    private final ParticipationService participationService;
    private final UserService userService;

    @Autowired
    public UserController(EventService eventService,
                          ParticipationService participationService,
                          UserService userService) {
        this.eventService = eventService;
        this.participationService = participationService;
        this.userService = userService;
    }

    /**
     * Создание нового события пользователем
     *
     * @param userId          id пользователя
     * @param requestEventDto Dto объект события
     * @return EventFullDto
     */
    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable @Positive long userId,
                                    @RequestBody @Valid RequestEventDto requestEventDto) {
        log.info("Пользователь id = {}. Создание события: {}", userId, requestEventDto);
        return eventService.createEvent(userId, requestEventDto);
    }

    /**
     * Обновление события созданного пользователем.
     * Обновить можно только не опубликованное событие
     *
     * @param userId          id пользователя
     * @param requestEventDto Dto объект события
     * @return EventFullDto
     */
    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable @Positive long userId,
                                    @RequestBody @Valid RequestEventDto requestEventDto) {
        log.info("Пользователь id = {}. Обновление события: {}", userId, requestEventDto);
        return eventService.updateEvent(userId, requestEventDto);
    }

    /**
     * Получение всех событий в краткой форме, созданных текущим пользователем
     *
     * @param userId id пользователя
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return List EventShortDto
     */
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getCreatorEvents(@PathVariable @Positive long userId,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получение всех событий созданных пользователем Id = {}\n" +
                "Параметры пагинации: from = {} size = {}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    /**
     * Получение полной информации о событии созданным текущим пользователем
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto
     */
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByCreator(@PathVariable @Positive long userId,
                                          @PathVariable @Positive long eventId) {
        log.info("Получение события id = {} создателя id = {}", eventId, userId);
        return eventService.getEventByCreator(userId, eventId);
    }

    /**
     * Отмена события созданного текущим пользователем
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto
     */
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEventByCreator(@PathVariable @Positive long userId,
                                             @PathVariable @Positive long eventId) {
        log.info("Отмена события id = {} создателя id = {}", eventId, userId);
        return eventService.cancelEventByCreator(userId, eventId);
    }

    /**
     * Получение списка заявок на участие в событии созданным пользователем
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return List ParticipationRequestDto
     */
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipantsOfEvent(@PathVariable @Positive long userId,
                                                                @PathVariable @Positive long eventId) {
        log.info("Получение заявок на участие события id = {} создателя id = {}", eventId, userId);
        return participationService.getParticipantsOfEvent(userId, eventId);
    }

    /**
     * Подтверждение заявки на участие в событии созданным пользователем
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @param reqId   id заявки на участие
     * @return ParticipationRequestDto
     */
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipation(@PathVariable @Positive long userId,
                                                        @PathVariable @Positive long eventId,
                                                        @PathVariable @Positive long reqId) {
        log.info("Подтверждение заявки на участие id = {} события id = {} создателя id = {}", reqId, eventId, userId);
        return participationService.setParticipationStatus(userId, eventId, reqId, true);
    }

    /**
     * Отклонение заявки на участие в событии созданным пользователем
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @param reqId   id заявки на участие
     * @return ParticipationRequestDto
     */
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipation(@PathVariable @Positive long userId,
                                                       @PathVariable @Positive long eventId,
                                                       @PathVariable @Positive long reqId) {
        log.info("Отклонение заявки на участие id = {} события id = {} создателя id = {}", reqId, eventId, userId);
        return participationService.setParticipationStatus(userId, eventId, reqId, false);
    }

    /**
     * Получение заявок текущего пользователя на участие в событиях
     *
     * @param userId id пользователя
     * @return List ParticipationRequestDto
     */
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserParticipation(@PathVariable @Positive long userId) {
        log.info("Запрос событий, в которых учавсвует пользователь id = {}", userId);
        return participationService.getUserParticipation(userId);
    }

    /**
     * Подача заявки от текущего пользователя на участие в событии
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return ParticipationRequestDto
     */
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto newParticipation(@PathVariable @Positive long userId,
                                                    @RequestParam @Positive long eventId) {
        log.info("Запрос на участие в событии id = {} от пользователя id = {}", eventId, userId);
        return participationService.newParticipation(userId, eventId);
    }

    /**
     * Отмена заявки на участие в событии, поданной текущим пользователем
     *
     * @param userId    id пользователя
     * @param requestId id события
     * @return ParticipationRequestDto
     */
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipation(@PathVariable @Positive long userId,
                                                       @PathVariable @Positive long requestId) {
        log.info("Отмена запроса id = {} на участие в событии от пользователя id = {}", requestId, userId);
        return participationService.cancelParticipation(userId, requestId);
    }

    /**
     * Подписка текущего пользователя на автора событий
     *
     * @param subId  id пользователя
     * @param userId id автора событий
     * @return ResponseEntity Object
     */
    @PostMapping("/{subId}/subscribes/{userId}")
    public ResponseEntity<Object> subscribeOnUser(@PathVariable @Positive long subId,
                                                  @PathVariable @Positive long userId) {
        log.info("Запрос подписки от пользователя id {} на пользователя id {}", subId, userId);
        return userService.subOnUser(subId, userId);
    }

    /**
     * Отписка текущего пользователя от автора событий
     *
     * @param subId  id пользователя
     * @param userId id автора событий
     * @return ResponseEntity Object
     */
    @DeleteMapping("/{subId}/subscribes/{userId}")
    public ResponseEntity<Object> unsubscribeOnUser(@PathVariable @Positive long subId,
                                                    @PathVariable @Positive long userId) {
        log.info("Запрос отписки от пользователя id {} на пользователя id {}", subId, userId);
        return userService.unsubOnUser(subId, userId);
    }

    /**
     * Получение списка подписок (авторов) текущего пользователя
     *
     * @param subId id пользователя
     * @return List UserShortDto
     */
    @GetMapping("/{subId}/subscribes")
    public List<UserShortDto> getAuthorsToSub(@PathVariable @Positive long subId) {
        log.info("Запрос получения списка подписок пользователя id = {}", subId);
        return userService.getSubscribes(subId);
    }

    /**
     * Получение списка подпищиков автора
     *
     * @param userId id автора
     * @return List<UserShortDto>
     */
    @GetMapping("/subscribes/{userId}")
    public List<UserShortDto> getSubscribers(@PathVariable @Positive long userId) {
        log.info("Запрос получения списка подписок пользователя id = {}", userId);
        return userService.getUserSubscribers(userId);
    }

    /**
     * Получение списка событий автора. Запрос от подпищика
     *
     * @param subId  id пользователя
     * @param userId id автора
     * @return List EventShortDto
     */
    @GetMapping("/{subId}/subscribes/{userId}/events")
    public List<EventShortDto> getAuthorEvents(@PathVariable @Positive long subId,
                                               @PathVariable @Positive long userId,
                                               @RequestParam(required = false, defaultValue = "true") boolean actual) {
        log.info("Запрос получения подпищиком id = {} событий пользователя id = {}", subId, userId);
        return eventService.getUserEventsToSub(subId, userId, actual);
    }
}
