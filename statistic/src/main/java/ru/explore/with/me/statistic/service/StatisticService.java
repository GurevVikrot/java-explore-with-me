package ru.explore.with.me.statistic.service;

import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    Map<String, String> saveStatistic(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistic(String start, String end, List<String> uris, boolean unique);
}
