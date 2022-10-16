package ru.explore.with.me.repository.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.participation.Participation;
import ru.explore.with.me.util.ParticipantStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByEventId(long eventId);

    @Query(value =
            "UPDATE participants " +
                    "SET status = ?2 " +
                    "WHERE event_id = ?1 " +
                    "AND status = ?3", nativeQuery = true)
    void changeParticipantsStatusOfEvent(long eventId, String newStatus, String oldStatus);

    @Query(value =
            "UPDATE participants " +
                    "SET status = ?2 " +
                    "WHERE event_id = ?1 ", nativeQuery = true)
    void changeParticipantsStatusOfEvent(long eventId, String newStatus);

    List<Participation> findAllByUserId(long userId);

    boolean existsByUserIdAndEventId(long userId, long eventId);

    @Query(value =
            "SELECT SUM(event_id) " +
                    "FROM  participants " +
                    "WHERE event_id = ? " +
                    "AND status = ? " +
                    "GROUP BY (event_id)", nativeQuery = true )
    Optional<Integer> getSumByEventIdAndStatusIs(Long eventId, String status);
}
