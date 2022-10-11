package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.service.category.CategoryService;
import ru.explore.with.me.service.compilation.CompilationService;
import ru.explore.with.me.service.event.EventService;
import ru.explore.with.me.service.user.UserService;
import ru.explore.with.me.util.EventStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
public class AdminController {
    private final UserService userService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;

    @Autowired
    public AdminController(UserService userService,
                           EventService eventService,
                           CategoryService categoryService,
                           CompilationService compilationService) {
        this.userService = userService;
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.compilationService = compilationService;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение всех пользователей");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public UserDto createUser(
            @RequestBody @Valid UserDto userDto) {
        log.info("Запрос создание пользователя {}", userDto.toString());
        return userService.createUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(
            @PathVariable @Positive long userId) {
        log.info("Запрос на удаление пользователя id = {}", userId);
        return userService.deleteUser(userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventStatus> states,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение событий параметры:\n" +
                "users {} \n" +
                "states {} \n" +
                "categories {}\n" +
                "rangeStart {}\n" +
                "rangeEnd {}\n" +
                "from {}\n" +
                "size{} \n", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsToAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Запрос на создание категории: {}", categoryDto.toString());
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Запрос на обновление категории: {}", categoryDto.toString());
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public String deleteCategory(@PathVariable @Positive int catId) {
        log.info("Запрос на удаление категории: {}", catId);
        return categoryService.deleteCategory(catId);
    }

    @PostMapping("/compilations")
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Запрос на создание подборки: {}", compilationDto);
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public String deleteCompilation(@PathVariable @Positive long compId) {
        log.info("Запрос на удаление подборки id = {}", compId);
        return compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public String addEventToCompilation(@PathVariable @Positive long compId,
                                        @PathVariable @Positive long eventId) {
        log.info("Запрос на добавление event: {} к подборке: {}", eventId, compId);
        return compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public String removeEventFromCompilation(@PathVariable @Positive long compId,
                                             @PathVariable @Positive long eventId) {
        log.info("Запрос на удаление event: {} из подборки: {}", eventId, compId);
        return compilationService.removeEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public String pinCompilation(@PathVariable @Positive long compId) {
        log.info("Запрос на закрепление подборки: {}", compId);
        return compilationService.pinCompilation(compId, true);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public String unpinCompilation(@PathVariable @Positive long compId) {
        log.info("Запрос на открепление подборки: {}", compId);
        return compilationService.pinCompilation(compId, false);
    }
}
