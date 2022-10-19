package ru.explore.with.me.repository.user.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.user.subscribe.Subscribe;
import ru.explore.with.me.model.user.subscribe.SubscribeId;

/**
 * Jpa репозиторий сущности Subscribe.
 * Ключ таблицы составной из двух полей.
 */
public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribeId> {
}
