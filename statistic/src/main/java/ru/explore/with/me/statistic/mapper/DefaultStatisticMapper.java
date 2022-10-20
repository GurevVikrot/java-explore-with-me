package ru.explore.with.me.statistic.mapper;

import org.springframework.stereotype.Component;
import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;
import ru.explore.with.me.statistic.model.EndpointHit;
import ru.explore.with.me.statistic.model.ViewStats;

/**
 * Мапер Dto объектов сервиса статистики
 */
@Component
public class DefaultStatisticMapper implements StatisticMapper {
    @Override
    public EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp().trim(),
                endpointHitDto.getUri().trim(),
                endpointHitDto.getIp().trim(),
                endpointHitDto.getTimestamp());
    }

    @Override
    public ViewStatsDto toViewDto(ViewStats viewStats) {
        return new ViewStatsDto(
                viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits());
    }
}
