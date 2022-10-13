package ru.explore.with.me.service.event;

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
import ru.explore.with.me.repository.category.CategoryRepository;
import ru.explore.with.me.repository.event.EventRepository;
import ru.explore.with.me.repository.participation.ParticipationRepository;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.util.EventStatus;
import ru.explore.with.me.util.ParticipantStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DbEventService implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final ParticipationRepository participationRepository;
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
                          EventMapper eventMapper,
                          EventClient eventClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.participationRepository = participationRepository;
        this.eventMapper = eventMapper;
        this.eventClient = eventClient;
    }

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
     * TODO доделать
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
    public List<EventFullDto> getEventsToAdmin(
            List<Long> users,
            List<EventStatus> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {
        return eventRepository.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение события по eго идентификатору. В Случае отсутвия события выбрасывается исключение.
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
        if (requestEventDto.getParticipantLimit() == 0 || requestEventDto.getRequestModeration() == null) {
            event.setRequestModeration(false);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * TODO доделать
     *
     * @param userId
     * @param requestEventDto
     * @return
     */
    @Override
    public EventFullDto updateEvent(long userId, RequestEventDto requestEventDto) {
        checkUserExist(userId);
        Event event = updateEventFromDto(requestEventDto, requestEventDto.getEventId(), true);
        event.setStatus(EventStatus.WAITING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByCreator(long userId, long eventId) {
        checkUserExist(userId);

        EventFullDto event = getEvent(eventId, null);

        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Событие принадлежит другому пользователю");
        }

        return event;
    }

    @Override
    public EventFullDto cancelEventByCreator(long userId, long eventId) {
        checkUserExist(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не сущесвует"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            event.setStatus(EventStatus.ENDED);
            return eventMapper.toEventFullDto(eventRepository.save(event));
        }

        throw new ValidationException("Возможно отменить событие только ожидающее подтверждения модератором");
    }

    /**
     * TODO
     * @param text
     * @param categories
     * @param paid
     * @param rangeStart
     * @param rangeEnd
     * @param onlyAvailable
     * @param sort
     * @param from
     * @param size
     * @param request
     * @return
     */
    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               Set<Integer> categories,
                                               boolean paid, String rangeStart,
                                               String rangeEnd, boolean onlyAvailable,
                                               String sort, int from, int size,
                                               HttpServletRequest request) {
        eventClient.sendStatistic(request);
        return null;
    }

    @Override
    public EventFullDto editEventByAdmin(RequestEventDto eventDto, long eventId) {
        if (eventDto == null) {
            throw new ValidationException("Тело запроса null, проверьте отправляемые данные");
        }

        return eventMapper.toEventFullDto(eventRepository.save(updateEventFromDto(eventDto,eventId,false)));
    }

    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));
        checkEventDate(event.getEventDate(), TIME_BEFORE_PUBLISH);

        if (!event.getStatus().equals(EventStatus.WAITING) || event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Событие уже опубликовано или не ожидает публикации");
        }

        event.setStatus(EventStatus.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));

        if (!event.getStatus().equals(EventStatus.WAITING) || event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Событие уже опубликовано или не ожидает публикации");
        }

        event.setStatus(EventStatus.REJECTED);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event updateEventFromDto(RequestEventDto eventDto, long eventId, boolean isUser) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление невозможно"));

        if (isUser) {
            checkEventDate(eventDto.getEventDate(), TIME_BEFORE_CREATE);
            if (!event.getStatus().equals(EventStatus.WAITING) || !event.getStatus().equals(EventStatus.REJECTED)) {
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
                participationRepository.changeParticipantsStatusOfEvent(
                        event.getId(), ParticipantStatus.PENDING.toString());
            }
        }
        // В случае если обновляется модерация в не зависимости true || false, все запросы уходят на пересогласование
        // требуется повторная отправка запросов (обновление) т.к. их может быть больше лимита
        // в случае false одобрять сразу все безсмысленно по этой же причине
        if (eventDto.getRequestModeration() != null && event.getParticipantLimit() > 0) {
            event.setRequestModeration(eventDto.getRequestModeration());
            participationRepository.changeParticipantsStatusOfEvent(
                    event.getId(), ParticipantStatus.PENDING.toString());
        }

        return event;
    }

    private void checkEventDate(LocalDateTime eventDate, int hours) {
        if (eventDate.plusHours(hours).isBefore(eventDate)) {
            throw new ValidationException("Время начала события должно быть не ранее " + hours +
                    " часов от момента его создания или публикации");
        }
    }

    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователя не существует");
        }
    }
}
