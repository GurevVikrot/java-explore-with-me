package ru.explore.with.me.service.user;

import org.springframework.http.ResponseEntity;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    String deleteUser(long userId);

    ResponseEntity<Object> subOnUser(long subId, long userId);

    ResponseEntity<Object> unsubOnUser(long subId, long userId);

    List<UserShortDto> getSubscribes(long subId);

    List<UserShortDto> getUserSubscribers(long userId);
}
