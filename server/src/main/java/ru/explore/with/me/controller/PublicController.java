package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.service.category.CategoryService;
import ru.explore.with.me.service.compilation.CompilationService;
import ru.explore.with.me.service.event.EventService;
import ru.explore.with.me.service.user.UserService;
import ru.explore.with.me.util.EventSort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
public class PublicController {
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    @Autowired
    public PublicController(EventService eventService,
                            CompilationService compilationService,
                            CategoryService categoryService) {
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.categoryService = categoryService;
    }

    /**
     * Публичный эндпоинт получения событий с возмгожностью фильтрациии.
     * Стандатрные условия филтрации:
     *      1. событие должно быть опубликовано
     *      2. Если не указан диапазон,  [rangeStart-rangeEnd], то нужно выгружать события,
     *      которые произойдут позже текущей даты и времени
     * @param text текст для поиска в содержимом аннотации и подробном описании события
     * @param categories список идентификаторов категорий в которых будет вестись поиск
     * @param paid поиск только платных/бесплатных событий
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort Вариант сортировки: по дате события или по количеству просмотров
     * @param from количество событий, которые нужно пропустить для формирования текущего набора
     * @param size количество событий в наборе
     * @param request данные запроса для отправки статистики
     * @return List EventShortDto
     */
    @GetMapping("/events")
    public List<EventShortDto> getEvents(
            @RequestParam String text,
            @RequestParam List<Integer> categories,
            @RequestParam boolean paid,
            @RequestParam LocalDateTime rangeStart,
            @RequestParam LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam EventSort sort,
            @RequestParam int from,
            @RequestParam int size,
            HttpServletRequest request) {
        return eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    /**
     * Публичный эндпоин. Получение подробной информации о событии по id
     * @param id Id события
     * @return EventFullDto
     */
    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable @Positive long id,
                                     HttpServletRequest request) {
        log.info("Публичный запрос на получение события id = {}", id);
        return eventService.getEvent(id, request);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Публичный запрос на получение подборок с параметрами\n  pinned = {}, from = {}, size = {}",
                pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable @Positive long compId) {
        log.info("Публичный запрос на получение подборки по id = {}", compId);
        return compilationService.getCompilation(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Публичный запрос на получение категорий\n from = {}, size = {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable @Positive int catId) {
        log.info("Публичный запрос на получение категории по id = {}", catId);
        return categoryService.getCategory(catId);
    }
}
