package ru.explore.with.me.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import ru.explore.with.me.model.category.Category;
import ru.explore.with.me.model.participation.Participation;
import ru.explore.with.me.util.EventStatus;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.Location;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "id")
    private User creator;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "cost")
    private int cost;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Where(clause = "status='CONFIRMED'")
    private List<Participation> participations;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "published", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "lat", nullable = false)
    private float lat;

    @Column(name = "lon")
    private float lon;
}
