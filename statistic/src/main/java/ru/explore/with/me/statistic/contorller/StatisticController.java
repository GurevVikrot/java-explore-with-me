package ru.explore.with.me.statistic.contorller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;
import ru.explore.with.me.statistic.service.StatisticService;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Validated
public class StatisticController {

    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping("/hit")
    public Map<String, String> hitEndpoint(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Получена статистике для сохранения: {}", endpointHitDto);
        return statisticService.saveStatistic(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistic(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) boolean unique) {
        log.info("Запрос статистики за период start = {}, end = {} \n uris = {}, unique = {}",
                start, end, uris, unique);
        return  statisticService.getStatistic(start, end, uris, unique);
    }
}
