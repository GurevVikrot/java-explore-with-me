package ru.explore.with.me.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.explore.with.me.client.event.EventClient;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.mapper.event.EventMapper;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.subscribe.SubscribeId;
import ru.explore.with.me.repository.category.CategoryRepository;
import ru.explore.with.me.repository.event.EventRepository;
import ru.explore.with.me.repository.participation.ParticipationRepository;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.repository.user.subscribe.SubscribeRepository;
import ru.explore.with.me.util.EventSort;
import ru.explore.with.me.util.EventStatus;
import ru.explore.with.me.util.ParticipantStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса событий работающая с БД
 */
@Service
@Slf4j
public class DbEventService implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRepository participationRepository;
    private final SubscribeRepository subscribeRepository;
    private final EventMapper eventMapper;
    private final EventClient eventClient;
    // Количество часов, на количество которых дата начала события должна быть позже
    // относительно момента создания/обновления
    private final int TIME_BEFORE_CREATE = 2;
    private final int TIME_BEFORE_PUBLISH = 1;

    @Autowired
    public DbEventService(EventRepository eventRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ParticipationRepository participationRepository,
                          SubscribeRepository subscribeRepository,
                          EventMapper eventMapper,
                          EventClient eventClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.participationRepository = participationRepository;
        this.subscribeRepository = subscribeRepository;
        this.eventMapper = eventMapper;
        this.eventClient = eventClient;
    }

    /**
     * Получение списка событий пользователя. В случае если пользователь не найден выбрасывается исключение.
     *
     * @param userId id пользователя
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return List EventShortDto
     */
    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        checkUserExist(userId);
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByCreator(userId, page).stream()
                .peek(event -> event.getParticipations().stream()
                        .filter(participant -> participant.getStatus().equals(ParticipantStatus.CONFIRMED))
                        .collect(Collectors.toList()))
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение списка событий с фильтрацией. Параметры могут быть null или empty
     * Заполнение списка заявок на участие происходит для каждого события индивидуально
     *
     * @param users      список id пользователей, чьи события нужно найти
     * @param states     список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd   дата и время не позже которых должно произойти событие
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       количество событий в наборе
     * @return List EventFullDto
     */
    @Override
    public List<EventFullDto> getEventsToAdmin(List<Long> users,
                                               List<EventStatus> states,
                                               List<Integer> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               int from,
                                               int size) {
        return eventRepository.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(eventMapper::toEventFullDto)
                .peek(event -> event.setConfirmedRequests(participationRepository
                        .getSumByEventIdAndStatusIs(event.getId(), ParticipantStatus.CONFIRMED.toString())
                        .orElse(0)))
                .collect(Collectors.toList());
    }

    /**
     * Получение события по eго идентификатору. В Случае отсутствия события выбрасывается исключение.
     * Метод отправляет статистику в сервис статистики
     *
     * @param id      Id События
     * @param request Данные запроса для отправки в сервис статистики. В случае null статистика не отравляется
     * @return EventFullDto or NotFoundException
     */
    @Override
    public EventFullDto getEvent(long id, HttpServletRequest request) {
        if (request != null) {
            eventClient.sendStatistic(request);
        }

        return eventMapper.toEventFullDto(eventRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Событие не найдено")));
    }

    /**
     * Создание пользователем нового события.
     * Время начала события должно быть позже его создания на 2 часа, выбрасывется исключение.
     * Если пользователя или категории не существует, выбрасывается исключение.
     *
     * @param userId          Id пользователя
     * @param requestEventDto Dto объект события.
     * @return EventFullDto or NotFoundException or ValidationException
     */
    @Override
    public EventFullDto createEvent(long userId, RequestEventDto requestEventDto) {
        checkEventDate(requestEventDto.getEventDate(), TIME_BEFORE_CREATE);

        Event event = eventMapper.toNewEvent(requestEventDto);

        event.setCreator(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")));
        event.setCategory(categoryRepository.findById(requestEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория не найдена")));

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * Обновление события пользователем. Пользователь должен быть создателем события
     *
     * @param userId          id пользователя
     * @param requestEventDto Dto объект события для обновления
     * @return EventFullDto
     */
    @Override
    public EventFullDto updateEvent(long userId, RequestEventDto requestEventDto) {
        checkUserExist(userId);
        Event event = updateEventFromDto(requestEventDto, requestEventDto.getEventId(), true);
        event.setStatus(EventStatus.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * Получение подробной информации о событии пользователя. Пользователь должен быть создателем события
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto or NotFoundException or ValidationException
     */
    @Override
    public EventFullDto getEventByCreator(long userId, long eventId) {
        checkUserExist(userId);

        EventFullDto event = getEvent(eventId, null);

        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Событие принадлежит другому пользователю");
        }

        return event;
    }

    /**
     * Отмена события пользователем. Невозможно отменить событие, которое уже опубликовано
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return EventFullDto or NotFoundException or ValidationException
     */
    @Override
    public EventFullDto cancelEventByCreator(long userId, long eventId) {
        checkUserExist(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            event.setStatus(EventStatus.CANCELED);
            return eventMapper.toEventFullDto(eventRepository.save(event));
        }

        throw new ValidationException("Возможно отменить событие только ожидающее подтверждения модератором");
    }

    /**
     * Поиск событий по фильтрам. Все фильтры могут быть default, null, empty
     * Возвращается краткая информация о событиях
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
    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Integer> categories,
                                               boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               boolean onlyAvailable,
                                               EventSort sort,
                                               int from,
                                               int size,
                                               HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("Некорректный промежуток времени." +
                        " Дата окончания промежутка раньше, чем начало промежутка.");
            }
        }

        eventClient.sendStatistic(request);

        if (categories != null) {
            categories = categories.stream().distinct().collect(Collectors.toList());
        }

        if (text != null) {
            text = text.trim();
        }

        List<EventShortDto> events = eventRepository.findAllByFilter(
                        text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size).stream()
                .map(eventMapper::toShortEventDto)
                .peek(event -> event.setConfirmedRequests(participationRepository
                        .getSumByEventIdAndStatusIs(event.getId(), ParticipantStatus.CONFIRMED.toString())
                        .orElse(0)))
                .collect(Collectors.toList());

        // Сотрировка если по кол-ву просмотров

        if (sort.equals(EventSort.VIEWS)) {
            events = events.stream()
                    .sorted(Comparator.comparingInt(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return events;
    }

    /**
     * Изменение события без валидации
     *
     * @param eventDto Dto объект события для обновления
     * @param eventId  id события
     * @return EventFullDto
     */
    @Override
    public EventFullDto editEventByAdmin(RequestEventDto eventDto, long eventId) {
        if (eventDto == null) {
            throw new ValidationException("Тело запроса null, проверьте отправляемые данные");
        }

        return eventMapper.toEventFullDto(eventRepository.save(updateEventFromDto(eventDto, eventId, false)));
    }

    /**
     * Публикация события
     *
     * @param eventId id события
     * @return EventFullDto
     */
    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));
        checkEventDate(event.getEventDate(), TIME_BEFORE_PUBLISH);

        if (!event.getStatus().equals(EventStatus.PENDING) || event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Событие уже опубликовано или не ожидает публикации");
        }

        event.setStatus(EventStatus.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * Отказ в публикации события
     *
     * @param eventId id события
     * @return EventFullDto
     */
    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));

        if (!event.getStatus().equals(EventStatus.PENDING) || event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Событие уже опубликовано или не ожидает публикации");
        }

        event.setStatus(EventStatus.CANCELED);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * Получение списка событий автора для подпищика
     *
     * @param subId  id пользователя
     * @param userId id автора
     * @return List EventShortDto
     */
    @Override
    public List<EventShortDto> getUserEventsToSub(long subId, long userId) {
        if (!subscribeRepository.existsById(new SubscribeId(userId, subId))) {
            throw new NotFoundException("Вы не являетесь подпищиком пользователя");
        }

        return eventRepository.findAllToSub(userId, EventStatus.PUBLISHED.toString()).stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод для получения обновленного объекта Event. Используется для обновления как пользовательских запросов,
     * так и админских
     *
     * @param eventDto Дто объект события с новыми полями
     * @param eventId  Id события
     * @param isUser   Флаг, обновление от пользователя = true, от админа = false
     * @return Event
     */
    private Event updateEventFromDto(RequestEventDto eventDto, long eventId, boolean isUser) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));

        // Пользователь не может обновить уже опубликованное событие
        // и не может выставить дату начала события раньше 2-ух часов от текущего момента
        if (isUser && eventDto.getEventDate() != null) {
            checkEventDate(eventDto.getEventDate(), TIME_BEFORE_CREATE);
            if (event.getStatus().equals(EventStatus.PUBLISHED)) {
                throw new ValidationException("Невозможно обновить опубликованное событие");
            }
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle().trim());
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation().trim());
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription().trim());
        }

        if (eventDto.getCategory() != 0) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория не найдена")));
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            if (eventDto.getParticipantLimit() == 0) {
                event.setParticipations(List.of());
                event.setRequestModeration(false);
            } else if (eventDto.getParticipantLimit() > event.getParticipantLimit()) {
                event.setParticipantLimit(eventDto.getParticipantLimit());
            } else if (eventDto.getParticipantLimit() < event.getParticipantLimit()) {
                event.setParticipantLimit(eventDto.getParticipantLimit());

                // Если количество участников при обновлении уменьшено, то все запросы участия
                // уходят на новое согласование в не зависимости от необходимости их одобрения
                try {
                    participationRepository.changeParticipantsStatusOfEvent(
                            event.getId(), ParticipantStatus.PENDING.toString());
                } catch (Exception e) {
                    log.info("Во время попытки изменить статусы запросов на участие в событии что-то пошло не так" +
                            e.getMessage());
                }

            }
        }
        // В случае если обновляется модерация в не зависимости true || false, все запросы уходят на пересогласование
        // требуется повторная отправка запросов (обновление) т.к. их может быть больше лимита
        // в случае false одобрять сразу все бессмысленно по этой же причине
        if (eventDto.getRequestModeration() != null && event.getParticipantLimit() > 0) {
            event.setRequestModeration(eventDto.getRequestModeration());
            try {
                participationRepository.changeParticipantsStatusOfEvent(
                        event.getId(), ParticipantStatus.PENDING.toString());
            } catch (Exception e) {
                log.info("Во время попытки изменить статусы запросов на участие в событии что-то пошло не так" +
                        e.getMessage());
            }
        }

        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }

        return event;
    }

    /**
     * Проверка даты события на соответствие относительно текущего момента
     *
     * @param eventDate Дата события
     * @param hours     Часы, на сколько дата начала события должна быть позже относительно текущего момента
     */
    private void checkEventDate(LocalDateTime eventDate, int hours) {
        if (LocalDateTime.now().plusHours(hours).isAfter(eventDate)) {
            throw new ValidationException("Время начала события должно быть позже " + hours +
                    " часов от текущего момента");
        }
    }

    /**
     * Проверка существования пользователя в БД
     *
     * @param userId id пользователя
     */
    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователя не существует");
        }
    }
}
