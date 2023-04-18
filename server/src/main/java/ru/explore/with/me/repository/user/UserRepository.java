package ru.explore.with.me.repository.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.explore.with.me.model.user.User;

import java.util.List;

/**
 * Jpa репозиторий пользователей
 */
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> id, Pageable pageable);

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
                    "LEFT OUTER JOIN users as u ON u.id = s.sub_id " +
                    "WHERE s.user_id = ?1",
            nativeQuery = true)
    List<User> findSubscribers(long userId);
}
