package ru.explore.with.me.model.compilation;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс генерации id для таблицы БД
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompilationEventsId implements Serializable {
    private Long eventId;
    private Long compilationId;
}
