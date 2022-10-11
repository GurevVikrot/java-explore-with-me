package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public EventRepository(EventJpaRepository jpaRepository, EventDAO eventDAO) {
        this.jpaRepository = jpaRepository;
        this.eventDAO = eventDAO;
    }

    public List<Event> findAllToAdmin(List<Long> users, List<EventStatus> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
       return eventDAO.findAllToAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    public List<Event> findAllByCreator(long userId, Pageable pageable) {
        return jpaRepository.findAllByCreator(userId, pageable);
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
}
