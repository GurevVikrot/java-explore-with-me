package ru.explore.with.me.model.participation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.ParticipantStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity класс для запросов на учатие в событии
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participants")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @Column(name = "status")
    private ParticipantStatus status;

    @Column(name = "created")
    private LocalDateTime created;
}
