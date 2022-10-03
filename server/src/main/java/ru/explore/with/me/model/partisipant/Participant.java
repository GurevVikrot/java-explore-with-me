package ru.explore.with.me.model.partisipant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.ParticipantStatus;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participants")
public class Participant {
    @EmbeddedId
    private ParticipantId participantId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @Column(name = "status")
    private ParticipantStatus status;
}
