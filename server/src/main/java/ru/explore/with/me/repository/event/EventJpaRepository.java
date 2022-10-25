package ru.explore.with.me.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Jpa репозиторий сущности Event
 */
public interface EventJpaRepository extends JpaRepository<Event, Long> {
    /**
     * Поиск всех событий автора
     *
     * @param userId   id автора
     * @param pageable Пагинация
     * @return List Event
     */
    List<Event> findAllByCreatorId(long userId, Pageable pageable);

    /**
     * Поиск всех событий автора по статусу с датой позже текущего момента
     *
     * @param userId id автора
     * @param status Статус
     * @return List Event
     */
    @Query(value = "SELECT * FROM events AS e " +
            "WHERE e.creator = ?1 " +
            "AND e.status = ?2 " +
            "AND e.event_date > ?3", nativeQuery = true)
    List<Event> findAllToSub(long userId, String status, LocalDateTime now);

    @Query(value = "SELECT * FROM events AS e " +
            "WHERE e.creator = ?1 " +
            "AND e.status = ?2", nativeQuery = true)
    List<Event> findAllByCreatorAndStatus(long userId, String toString);
}
