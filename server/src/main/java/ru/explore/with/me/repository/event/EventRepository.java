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
}
