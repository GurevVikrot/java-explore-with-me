package ru.explore.with.me.repository.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.participation.Participation;

import java.util.List;
import java.util.Optional;

/**
 * Jpa репозиторий сущности Participation
 */
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByEventId(long eventId);

    /**
     * Смена статуса всех участий в событии по id события и статусу участия
     *
     * @param eventId   id события
     * @param newStatus Новый статус. ParticipantStatus.toString()
     * @param oldStatus Изменяемый статус. ParticipantStatus.toString()
     */
    @Query(value =
            "UPDATE participants " +
                    "SET status = ?2 " +
                    "WHERE event_id = ?1 " +
                    "AND status = ?3", nativeQuery = true)
    void changeParticipantsStatusOfEvent(long eventId, String newStatus, String oldStatus);

    /**
     * Смена статуса участия в событии по id события
     *
     * @param eventId   id события
     * @param newStatus Новый статус. ParticipantStatus.toString()
     */
    @Query(value =
            "UPDATE participants " +
                    "SET status = ?2 " +
                    "WHERE event_id = ?1 ", nativeQuery = true)
    void changeParticipantsStatusOfEvent(long eventId, String newStatus);

    /**
     * Поиск всех участий в событии по id пользователя
     *
     * @param userId id пользователя
     * @return List Participation
     */
    List<Participation> findAllByUserId(long userId);

    boolean existsByUserIdAndEventId(long userId, long eventId);

    /**
     * Выборка количества участий в событии по id события и статусу участия
     *
     * @param eventId id события
     * @param status  Статус участияParticipantStatus.toString()
     * @return Optional Integer
     */
    @Query(value =
            "SELECT SUM(event_id) " +
                    "FROM  participants " +
                    "WHERE event_id = ? " +
                    "AND status = ? " +
                    "GROUP BY (event_id)", nativeQuery = true)
    Optional<Integer> getSumByEventIdAndStatusIs(Long eventId, String status);
}
