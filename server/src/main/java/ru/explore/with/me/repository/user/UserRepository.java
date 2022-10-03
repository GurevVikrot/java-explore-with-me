package ru.explore.with.me.repository.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.model.user.User;

import java.util.Arrays;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllWhereIdIn(List<Long> id, Pageable pageable);
}
