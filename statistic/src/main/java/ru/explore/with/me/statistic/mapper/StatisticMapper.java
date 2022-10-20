package ru.explore.with.me.statistic.mapper;

import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;
import ru.explore.with.me.statistic.model.EndpointHit;
import ru.explore.with.me.statistic.model.ViewStats;

public interface StatisticMapper {
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    ViewStatsDto toViewDto(ViewStats viewStats);
}
