package ru.explore.with.me.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.model.event.Event;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations_events")
public class CompilationEvents {
    @EmbeddedId
    private CompilationEventsId id;

    @ManyToOne
    @MapsId("compilationId")
    @JoinColumn(name = "compilation_id", referencedColumnName = "id", nullable = false)
    private Compilation compilation;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;
}
