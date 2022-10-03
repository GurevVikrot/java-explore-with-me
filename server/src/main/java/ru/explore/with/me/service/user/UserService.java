package ru.explore.with.me.service.user;

import ru.explore.with.me.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    String deleteUser(long userId);
}
