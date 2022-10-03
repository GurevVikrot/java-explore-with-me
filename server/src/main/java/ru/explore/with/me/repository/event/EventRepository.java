package ru.explore.with.me.repository.event;

import org.springframework.stereotype.Component;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class EventRepository {
    private EventJpaRepository jpaRepository;
    private EventDAO eventDAO;

    public List<Event> findAllToAdmin(List<Long> users, List<EventStatus> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
       return eventDAO.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    public List<Event> findAllByCreator(long userId) {
        return jpaRepository.findAllByCreator(userId);
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
}
