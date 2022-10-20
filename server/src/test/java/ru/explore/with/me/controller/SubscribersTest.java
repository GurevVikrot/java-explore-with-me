package ru.explore.with.me.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.dto.event.RequestEventDto;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.model.category.Category;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.subscribe.Subscribe;
import ru.explore.with.me.model.user.subscribe.SubscribeId;
import ru.explore.with.me.repository.category.CategoryRepository;
import ru.explore.with.me.repository.event.EventRepository;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.repository.user.subscribe.SubscribeRepository;
import ru.explore.with.me.service.event.DbEventService;
import ru.explore.with.me.service.user.DbUserService;
import ru.explore.with.me.util.Location;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SubscribersTest {

    @Autowired
    private final DbUserService userService;
    @Autowired
    private final DbEventService eventService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final SubscribeRepository subscribeRepository;

    @BeforeEach
    void BeforeEach() {
        userService.createUser(new UserDto(0, "user@mail.ru", "user", null));
        userRepository.findById(1L).orElseThrow();

        userService.createUser(new UserDto(0, "sub@mail.ru", "sub", null));
        userRepository.findById(2L).orElseThrow();

        categoryRepository.save(new Category(null, "category", "description"));
        eventService.createEvent(
                1L,
                new RequestEventDto(
                        0,
                        "title",
                        "annotation",
                        "description",
                        1,
                        LocalDateTime.now().plusDays(2),
                        false,
                        0,
                        false,
                        new Location(0, 0)));
    }

    @Test
    public void correctSubscribeTest() {
        userService.subOnUser(2L, 1L);
        Subscribe subscribe = subscribeRepository.findById(new SubscribeId(1L, 2L)).orElseThrow();
        assertEquals(1L, subscribe.getUser().getId());
        assertEquals("user", subscribe.getUser().getName());
        assertEquals(2L, subscribe.getSub().getId());
        assertEquals("sub", subscribe.getSub().getName());
    }

    @Test
    public void sameIdUserSubTest() {
        assertThrows(ValidationException.class, () -> userService.subOnUser(1L, 1L));
    }

    @Test
    public void UserOrSubNotExist() {
        assertThrows(NotFoundException.class, () -> userService.subOnUser(2L, 99L));
        assertThrows(NotFoundException.class, () -> userService.subOnUser(99L, 1L));
    }

    @Test
    public void unsubTest() {
        userService.subOnUser(2L, 1L);
        assertTrue(subscribeRepository.existsById(new SubscribeId(1L, 2L)));
        userService.unsubOnUser(2L, 1L);
        assertFalse(subscribeRepository.existsById(new SubscribeId(1L, 2L)));
    }

    @Test
    public void unsubWhenUsersAndSubNotExist() {
        assertThrows(NotFoundException.class, () -> userService.unsubOnUser(2L, 1L));
        assertThrows(NotFoundException.class, () -> userService.unsubOnUser(99L, 1L));
        assertThrows(NotFoundException.class, () -> userService.unsubOnUser(2L, 99L));
    }

    @Test
    public void getUserSubscribesTest() {
        userService.subOnUser(2L, 1L);
        List<UserShortDto> subs = userService.getUserSubscribers(1L);
        assertNotNull(subs);
        assertFalse(subs.isEmpty());
        assertEquals(2L, subs.get(0).getId());

        List<UserShortDto> subs2 = userService.getUserSubscribers(2L);
        assertNotNull(subs2);
        assertTrue(subs2.isEmpty());

        assertThrows(NotFoundException.class, () -> userService.getUserSubscribers(99L));
    }

    @Test
    public void getSubscribesTest() {
        userService.subOnUser(2L, 1L);
        List<UserShortDto> subs = userService.getSubscribes(2L);
        assertNotNull(subs);
        assertFalse(subs.isEmpty());
        assertEquals(1L, subs.get(0).getId());

        List<UserShortDto> subs2 = userService.getSubscribes(1L);
        assertNotNull(subs2);
        assertTrue(subs2.isEmpty());

        assertThrows(NotFoundException.class, () -> userService.getUserSubscribers(99L));
    }

    @Test
    public void getEventsToSubscriber() {
        userService.subOnUser(2L, 1L);
        List<EventShortDto> events = eventService.getUserEventsToSub(2L, 1L);
        assertNotNull(events);
        assertTrue(events.isEmpty());

        Event event = eventRepository.findById(1L).orElseThrow();
        eventService.publishEvent(1L);
        events = eventService.getUserEventsToSub(2L, 1L);
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(1L, events.get(0).getId());
        assertEquals(event.getTitle(), events.get(0).getTitle());
        assertEquals(event.getAnnotation(), events.get(0).getAnnotation());
        assertEquals(event.getEventDate(), events.get(0).getEventDate());

        userService.subOnUser(1L, 2L);
        List<EventShortDto> events2 = eventService.getUserEventsToSub(1L, 2L);
        assertNotNull(events2);
        assertTrue(events2.isEmpty());

        assertThrows(NotFoundException.class, () -> eventService.getUserEventsToSub(99L, 2L));
        assertThrows(NotFoundException.class, () -> eventService.getUserEventsToSub(1L, 99L));
        assertThrows(NotFoundException.class, () -> eventService.getUserEventsToSub(99L, 99L));
    }
}