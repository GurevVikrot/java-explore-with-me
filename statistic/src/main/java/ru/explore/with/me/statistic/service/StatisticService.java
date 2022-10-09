package ru.explore.with.me.statistic.service;

import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;

import java.util.List;

public interface StatisticService {
    String saveStatistic(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistic(String start, String end, List<String> uris, boolean unique);
}
