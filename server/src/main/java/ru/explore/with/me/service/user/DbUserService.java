package ru.explore.with.me.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.mapper.user.UserMapper;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.model.user.subscribe.Subscribe;
import ru.explore.with.me.model.user.subscribe.SubscribeId;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.repository.user.subscribe.SubscribeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса сервиса пользователей работающая с БД
 */
@Service
public class DbUserService implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SubscribeRepository subRepository;

    @Autowired
    public DbUserService(
            UserMapper userMapper,
            UserRepository userRepository,
            SubscribeRepository subRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.subRepository = subRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Ошибка запроса, получен null, создание пользователя не возможно");
        }

        User user = userMapper.toUser(userDto);
        user.setCreated(LocalDateTime.now());

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "Пользователь удален";
        }

        throw new NotFoundException("Пользователь не найден");
    }

    @Override
    public ResponseEntity<Object> subOnUser(long subId, long userId) {
        if (subId == userId) {
            throw new ValidationException("Пользователь не может быть подписан сам на себя");
        }

        User user = getUserFromDb(userId);

        User sub = getUserFromDb(subId);

        Subscribe subscribe = new Subscribe(
                new SubscribeId(userId, subId), user, sub);
        subRepository.save(subscribe);
        return ResponseEntity.accepted().body("Вы подписались на пользователя");
    }

    @Override
    public ResponseEntity<Object> unsubOnUser(long subId, long userId) {
        Subscribe subscribe = subRepository.findById(new SubscribeId(userId, subId)).orElseThrow(
                () -> new NotFoundException("Подписка на пользователя не найдена"));
        subRepository.delete(subscribe);
        return ResponseEntity.accepted().body("Отписка успешна");
    }

    @Override
    public List<UserShortDto> getSubscribes(long subId) {
        userExist(subId);
        return userRepository.findUsersBySubId(subId).stream()
                .map(userMapper::toUserShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserShortDto> getUserSubscribers(long userId) {
        userExist(userId);
        return userRepository.findSubscribers(userId).stream()
                .map(userMapper::toUserShortDto)
                .collect(Collectors.toList());
    }

    private User getUserFromDb(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
    }

    private void userExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
