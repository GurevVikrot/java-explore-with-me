package ru.explore.with.me.model.user.subscribe;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс генерации ключа связующей таблицы подписок
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SubscribeId implements Serializable {
    private Long userId;
    private Long subId;
}
