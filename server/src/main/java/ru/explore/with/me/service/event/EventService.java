package ru.explore.with.me.service.event;

import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.util.EventSort;
import ru.explore.with.me.util.EventStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса событий
 */
public interface EventService {

    /**
     * Получение всех событий в краткой форме, созданных пользователем
     *
     * @param userId id пользователя
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return List EventShortDto
     */
    List<EventShortDto> getUserEvents(long userId, int from, int size);

    /**
     * Получение подробного списка событий для админа
     *
     * @param users      список id пользователей, чьи события нужно найти
     * @param states     список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd   дата и время не позже которых должно произойти событие
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора. default = 0
     * @param size       количество событий в наборе. default = 10
     * @return List<EventFullDto>
     */
    List<EventFullDto> getEventsToAdmin(List<Long> users, List<EventStatus> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    /**
     * Получение подробной информации о событии по id
     *
     * @param id Id события
     * @return EventFullDto
     */
    EventFullDto getEvent(long id, HttpServletRequest request);

    /**
     * Создание нового события пользователем
     *
     * @param userId          id пользователя
     * @param requestEventDto Dto объект события
     * @return EventFullDto
     */
    EventFullDto createEvent(long userId, RequestEventDto requestEventDto);

    /**
     * Обновление события в БД
     *
     * @param userId          id пользователя
     * @param requestEventDto Dto объект события для обновления
     * @return EventFullDto
     */
    EventFullDto updateEvent(long userId, RequestEventDto requestEventDto);

    /**
     * Получение полной информации о событии для его создателя
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto
     */
    EventFullDto getEventByCreator(long userId, long eventId);

    /**
     * Отмена события по инициативе создателя
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto
     */
    EventFullDto cancelEventByCreator(long userId, long eventId);

    /**
     * Метод получения событий с возможностью фильтрации.
     * Стандартные условия фильтрации:
     * 1. Событие должно быть опубликовано
     * 2. Если не указан диапазон,  [rangeStart-rangeEnd], то нужно выгружать события,
     * которые произойдут позже текущей даты и времени
     *
     * @param text          текст для поиска в содержимом аннотации и подробном описании события
     * @param categories    список идентификаторов категорий в которых будет вестись поиск
     * @param paid          поиск только платных/бесплатных событий
     * @param rangeStart    дата и время не раньше которых должно произойти событие
     * @param rangeEnd      дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort          Вариант сортировки: по дате события или по количеству просмотров
     * @param from          количество событий, которые нужно пропустить для формирования текущего набора
     * @param size          количество событий в наборе
     * @param request       данные запроса для отправки статистики
     * @return List EventShortDto
     */
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

    /**
     * Редактирование события админом без валидации
     *
     * @param eventId  id события
     * @param eventDto Dto объект события для обновления
     * @return EventFullDto
     */
    EventFullDto editEventByAdmin(RequestEventDto eventDto, long eventId);

    /**
     * Публикация события админом
     *
     * @param eventId id события
     * @return EventFullDto
     */
    EventFullDto publishEvent(long eventId);

    /**
     * Отказ в публикации события админом
     *
     * @param eventId id события
     * @return EventFullDto
     */
    EventFullDto rejectEvent(long eventId);

    /**
     * Получение списка событий автора. Запрос от подпищика
     *
     * @param subId  id пользователя
     * @param userId id автора
     * @return List EventShortDto
     */
    List<EventShortDto> getUserEventsToSub(long subId, long userId);
}
