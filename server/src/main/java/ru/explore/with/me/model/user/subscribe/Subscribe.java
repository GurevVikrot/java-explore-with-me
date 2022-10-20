package ru.explore.with.me.model.user.subscribe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.explore.with.me.model.user.User;

import javax.persistence.*;

/**
 * Entity класс для подписки на пользователя.
 * Ключ таблицы генерируемый из двух полей id подпищика и автора
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "subscribers")
public class Subscribe {
    @EmbeddedId
    private SubscribeId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("subId")
    @JoinColumn(name = "sub_id", referencedColumnName = "id", nullable = false)
    private User sub;
}
