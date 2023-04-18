package ru.explore.with.me.statistic.contorller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;
import ru.explore.with.me.statistic.service.StatisticService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Контроллер сервиса статистики
 */
@RestController
@Slf4j
@Validated
public class StatisticController {

    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Эндпоинт записи статистики обращения к сервисам
     *
     * @param endpointHitDto Dto объект статистики обращения
     * @return Map String, String
     */
    @PostMapping("/hit")
    public Map<String, String> hitEndpoint(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Получена статистике для сохранения: {}", endpointHitDto);
        return statisticService.saveStatistic(endpointHitDto);
    }

    /**
     * Эндпоинт получения статистики по перечню uri и за определенный промежуток времени.
     *
     * @param start  Дата и время начала промежутка
     * @param end    Дата и время окончания промежутка
     * @param uris   Список uri
     * @param unique уникальные или нет ip обращений записанных в статистике
     * @return List ViewStatsDto
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistic(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) boolean unique) {
        log.info("Запрос статистики за период start = {}, end = {} \n uris = {}, unique = {}",
                start, end, uris, unique);
        return statisticService.getStatistic(start, end, uris, unique);
    }
}
