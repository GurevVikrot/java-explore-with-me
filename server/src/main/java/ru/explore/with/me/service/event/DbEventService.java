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
    private final EventMapper eventMapper;
    private final EventClient eventClient;

    @Autowired
    public DbEventService(EventRepository eventRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          EventMapper eventMapper,
                          EventClient eventClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.eventClient = eventClient;
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        checkUserExist(userId);
        Pageable page = PageRequest.of(from/size,size);
        return eventRepository.findAllByCreator(userId, page).stream()
                .peek(event -> event.getParticipations().stream()
                        .filter(participant -> participant.getStatus().equals(ParticipantStatus.CONFIRMED))
                        .collect(Collectors.toList()))
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    /**
     * TODO доделать
     * @param users
     * @param states
     * @param categories
     * @param rangeStart
     * @param rangeEnd
     * @param from
     * @param size
     * @return
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
        eventRepository.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return null;
    }

    /**
     * Получение события по eго идентификатору. В Случае отсутвия события выбрасывается исключение.
     *
     * @param id      Id События
     * @param request
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
     * @param userId Id пользователя
     * @param requestEventDto Dto объект события.
     * @return EventFullDto or NotFoundException or ValidationException
     */
    @Override
    public EventFullDto createEvent(long userId, RequestEventDto requestEventDto) {
        checkEventDate(requestEventDto);

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
     * @param userId
     * @param requestEventDto
     * @return
     */
    @Override
    public EventFullDto updateEvent(long userId, RequestEventDto requestEventDto) {
        checkUserExist(userId);
        checkEventDate(requestEventDto);

        Event eventFromDb = eventRepository.findById(requestEventDto.getEventId()).orElseThrow(
                () -> new NotFoundException("События не существует. Обновление не возможно"));

        if (!eventFromDb.getStatus().equals(EventStatus.WAITING) || !eventFromDb.getStatus().equals(EventStatus.CANCELED)) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }

        eventFromDb.setTitle(requestEventDto.getTitle().trim());
        eventFromDb.setAnnotation(requestEventDto.getAnnotation().trim());
        eventFromDb.setDescription(requestEventDto.getDescription().trim());
        eventFromDb.setCategory(categoryRepository.findById(requestEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category not found")));
        eventFromDb.setEventDate(requestEventDto.getEventDate());

        if (requestEventDto.getPaid() != null) {
            eventFromDb.setPaid(requestEventDto.getPaid());
        }

        eventFromDb.setParticipantLimit(requestEventDto.getParticipantLimit());

        if (requestEventDto.getParticipantLimit() == 0 || requestEventDto.getRequestModeration() == null) {
            eventFromDb.setRequestModeration(false);
        } else {
            eventFromDb.setRequestModeration(requestEventDto.getRequestModeration());
        }

        eventFromDb.setStatus(EventStatus.WAITING);

        return eventMapper.toEventFullDto(eventRepository.save(eventFromDb));
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

        if (event.getStatus().equals(EventStatus.WAITING)) {
            event.setStatus(EventStatus.CANCELED);
            return eventMapper.toEventFullDto(eventRepository.save(event));
        }

        throw new ValidationException("Возможно отменить событие только ожидающее подтверждения модератором");
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, Set<Integer> categories, boolean paid, String rangeStart, String rangeEnd, boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request) {
        eventClient.sendStatistic(request);
        return null;
    }

    private void checkEventDate(RequestEventDto requestEventDto) {
        if (requestEventDto.getEventDate().plusHours(2).isBefore(requestEventDto.getEventDate())) {
            throw new ValidationException("Время начала события должно быть на 2 часа позже момента его создания");
        }
    }

    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователя не существует");
        }
    }
}
