package ru.explore.with.me.mapper.user;

import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.model.user.User;

public interface UserMapper {
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
