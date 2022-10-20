package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.repository.participation.ParticipationRepository;
import ru.explore.with.me.util.EventSort;
import ru.explore.with.me.util.EventStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Обертка репозитория сущности Event. Содержит в себе Jpa и Jdbc
 */
@Component
@Transactional
public class EventRepository {
    private final EventJpaRepository jpaRepository;
    private final EventDAO eventDAO;

    private final ParticipationRepository participationRepository;

    @Autowired
    public EventRepository(EventJpaRepository jpaRepository,
                           EventDAO eventDAO,
                           ParticipationRepository participationRepository) {
        this.jpaRepository = jpaRepository;
        this.eventDAO = eventDAO;
        this.participationRepository = participationRepository;
    }

    /**
     * Метод выборки событий с динамическим составлением запроса к БД для запроса событий от админа.
     * Все параметры не обязательные, null или empty, за исключением from и size
     *
     * @param users      список id пользователей, чьи события нужно найти
     * @param states     список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd   дата и время не позже которых должно произойти событие
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       количество событий в наборе
     * @return List Event
     */
    public List<Event> findAllToAdmin(List<Long> users,
                                      List<EventStatus> states,
                                      List<Integer> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      int from,
                                      int size) {
        return eventDAO.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .peek((e) -> e.setParticipations(participationRepository.findAllByEventId(e.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Поиск всех событий автора
     *
     * @param userId   id автора
     * @param pageable Пагинация
     * @return List Event
     */
    public List<Event> findAllByCreator(long userId, Pageable pageable) {

        return jpaRepository.findAllByCreatorId(userId, pageable);
    }

    public List<Event> findAllById(List<Long> events) {
        return jpaRepository.findAllById(events);
    }

    public Optional<Event> findById(long id) {
        return jpaRepository.findById(id);
    }

    public Event save(Event event) {
        return jpaRepository.save(event);
    }

    public boolean existById(long eventId) {
        return jpaRepository.existsById(eventId);
    }

    /**
     * Метод выборки событий с динамическим составлением запроса к БД для запроса событий с фильтрацией.
     * Все параметры не обязательные, null или empty, за исключением sort, from и size
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
     * @return List Event
     */
    public List<Event> findAllByFilter(String text,
                                       List<Integer> categories,
                                       boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       boolean onlyAvailable,
                                       EventSort sort,
                                       int from,
                                       int size) {
        return eventDAO.findAllByFilter(
                        text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size).stream()
                .peek((e) -> e.setParticipations(participationRepository.findAllByEventId(e.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Поиск всех событий автора по статусу с датой позже текущего момента
     *
     * @param userId id автора
     * @param status Статус
     * @return List Event
     */
    public List<Event> findAllToSub(long userId, String status) {
        return jpaRepository.findAllToSub(userId, status, LocalDateTime.now());
    }
}