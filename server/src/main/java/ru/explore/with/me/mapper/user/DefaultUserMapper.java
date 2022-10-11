package ru.explore.with.me.mapper.user;

import org.springframework.stereotype.Component;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.model.user.User;

@Component
public class DefaultUserMapper implements UserMapper {
    @Override
    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId() == 0 ? null : userDto.getId(),
                userDto.getEmail(),
                userDto.getName(),
                null
        );
    }

    @Override
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreated()
        );
    }

    @Override
    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
