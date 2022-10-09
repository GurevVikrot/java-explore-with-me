package ru.explore.with.me.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.explore.with.me.client.event.EventClient;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.event.EventFullDto;
import ru.explore.with.me.dto.event.EventShortDto;
import ru.explore.with.me.service.category.CategoryService;
import ru.explore.with.me.service.compilation.CompilationService;
import ru.explore.with.me.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@Slf4j
public class PublicController {

    private final EventService eventService;
    private final EventClient eventClient;

    private final CompilationService compilationService;
    private final CategoryService categoryService;

    @Autowired
    public PublicController(EventService eventService,
                            EventClient eventClient,
                            CompilationService compilationService,
                            CategoryService categoryService) {
        this.eventService = eventService;
        this.eventClient = eventClient;
        this.compilationService = compilationService;
        this.categoryService = categoryService;
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(
            @RequestParam String text,
            @RequestParam Set<Integer> categories,
            @RequestParam boolean paid,
            @RequestParam String rangeStart,
            @RequestParam String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam String sort,
            @RequestParam int from,
            @RequestParam int size,
            HttpServletRequest request) {
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    /**
     * Публичный эндпоин. Получение подробноый информации о событии по id
     *
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
