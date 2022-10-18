package ru.explore.with.me.repository.user.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.model.user.subscribe.Subscribe;
import ru.explore.with.me.model.user.subscribe.SubscribeId;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribeId> {
    @Query(value =
            "SELECT u.id, u.name, u.email, u.created " +
                    "FROM subscribers  AS s " +
                    "LEFT OUTER JOIN users as u ON u.id = s.user_id " +
                    "WHERE s.sub_id = ?1",
    nativeQuery = true)
    List<User> findUsersBySubId(long subId);

    @Query(value =
            "SELECT u.id, u.name, u.email, u.created " +
                    "FROM subscribers  AS s " +
                    "LEFT OUTER JOIN users as u ON u.id = s.user_id " +
                    "WHERE s.user_id = ?1",
            nativeQuery = true)
    List<User> findSubscribers(long userId);
}
