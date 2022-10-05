package ru.explore.with.me.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.model.participation.Participation;
import ru.explore.with.me.util.ParticipantStatus;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByEventId(long eventId);

    @Query(value =
            "UPDATE participants " +
                    "SET status = ?2 " +
                    "WHERE event_id = ?1 " +
                    "AND status = ?3", nativeQuery = true)
    void changeParticipantsStatusOfEvent(long eventId, String newStatus, String oldStatus);

    List<Participation> findAllByUserId(long userId);

    boolean existByUserIdAndEventId(long userId, long eventId);

    int getSumByEventIdAndStatusIs(Long eventId, ParticipantStatus status);
}
