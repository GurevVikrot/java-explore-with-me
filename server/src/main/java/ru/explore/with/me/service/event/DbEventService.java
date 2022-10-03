package ru.explore.with.me.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.NewEventDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.mapper.event.EventMapper;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.repository.category.CategoryRepository;
import ru.explore.with.me.repository.event.EventRepository;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbEventService implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Autowired
    public DbEventService(EventRepository eventRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventFullDto> getUserEvents(long userId, int from, int size) {
        return eventRepository.findAllByCreator(userId).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

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
     * @param id Id События
     * @return EventFullDto or NotFoundException
     */
    @Override
    public EventFullDto getEvent(long id) {
        return eventMapper.toEventFullDto(eventRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Событие не найдено")));
    }

    /**
     * Создание пользователем нового события.
     * Время начала события должно быть позже его создания на 2 часа, выбрасывется исключение.
     * Если пользователя или категории не существует, выбрасывается исключение.
     * @param userId Id пользователя
     * @param newEventDto Dto объект события.
     * @return EventFullDto or NotFoundException or ValidationException
     */
    @Override
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(newEventDto.getEventDate().plusHours(2))) {
            throw new ValidationException("Время начала события должно быть на 2 часа позже момента его создания");
        }

        Event event = eventMapper.toNewEvent(newEventDto);

        event.setCreator(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")));
        event.setCategories(categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория не найдена")));
        if (newEventDto.getParticipantLimit() == 0 || newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(false);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }
}
