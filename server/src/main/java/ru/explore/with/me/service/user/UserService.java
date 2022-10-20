package ru.explore.with.me.service.user;

import org.springframework.http.ResponseEntity;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;

import java.util.List;

/**
 * Интерфейс сервиса пользователей
 */
public interface UserService {

    /**
     * Создание нового пользователя, осущесвляется Админ
     *
     * @param userDto Dto объект пользователя для создания
     * @return userDto
     */
    UserDto createUser(UserDto userDto);

    /**
     * Получение подробной информации о пользователях
     *
     * @param ids  список id запрашиваемых пользователей
     * @param from количество событий, которые нужно пропустить для формирования текущего набора
     * @param size количество событий в наборе
     * @return List UserDto
     */
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    /**
     * Удаление пользователя. В случае отсутствия пользователя выбрасывается исключение.
     *
     * @param userId id удаляемого пользователя
     * @return String or NotFoundExсeption
     */
    String deleteUser(long userId);

    /**
     * Подписка текущего пользователя на автора событий
     *
     * @param subId  id пользователя
     * @param userId id автора событий
     * @return ResponseEntity Object
     */
    ResponseEntity<Object> subOnUser(long subId, long userId);

    /**
     * Отписка текущего пользователя от автора событий
     *
     * @param subId  id пользователя
     * @param userId id автора событий
     * @return ResponseEntity Object
     */
    ResponseEntity<Object> unsubOnUser(long subId, long userId);

    /**
     * Получение списка подписок (авторов) текущего пользователя
     *
     * @param subId id пользователя
     * @return List UserShortDto
     */
    List<UserShortDto> getSubscribes(long subId);

    /**
     * Получение списка подпищиков автора
     *
     * @param userId id автора
     * @return List<UserShortDto>
     */
    List<UserShortDto> getUserSubscribers(long userId);
}
