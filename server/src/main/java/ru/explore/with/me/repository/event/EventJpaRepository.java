package ru.explore.with.me.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.event.Event;

import java.util.List;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
//    @Query(
//            value = "SELECT * FROM events " +
//                    "INNER JOIN partisipants ON events.id = partisipants.event_id " +
//                    "INNER JOIN users ON user.id = partisipants.user_id " +
//                    "WHERE partisipants.status = 'CONFIRMED'" +
//                    "AND event.creator = ?1;", nativeQuery = true)
    List<Event> findAllByCreator(long userId, Pageable pageable);

    @Query(value = "?1", nativeQuery = true)
    List<Event> findAllToAdmin(String sql);
}
