package ru.explore.with.me.mapper.user;

import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.model.user.User;

/**
 * Интерфейс маппинга пользователя User в его Dto
 */
public interface UserMapper {
    /**
     * Преобразование UserDto в User
     *
     * @param userDto Dto объект
     * @return User
     */
    User toUser(UserDto userDto);

    /**
     * Преобразование User в UserDto, его полная форма
     *
     * @param user Dto объект
     * @return User
     */
    UserDto toUserDto(User user);

    /**
     * Преобразование User в UserShortDto, его краткая форма
     *
     * @param user Dto объект
     * @return User
     */
    UserShortDto toUserShortDto(User user);
}
