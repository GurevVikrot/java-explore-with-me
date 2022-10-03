package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.event.Event;

import javax.persistence.EntityManager;
import java.util.List;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCreator(long userId);

    @Query(value = "?1", nativeQuery = true)
    List<Event> findAllToAdmin(String sql);
}
