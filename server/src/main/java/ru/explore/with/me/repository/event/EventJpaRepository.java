package ru.explore.with.me.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.event.Event;

import java.util.List;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCreatorId(long userId, Pageable pageable);
}
