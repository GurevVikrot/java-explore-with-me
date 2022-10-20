package ru.explore.with.me.statistic.service;

import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс сервиса статистики
 */
public interface StatisticService {
    /**
     * Сохранение статистики обращения к uri
     *
     * @param endpointHitDto Dto объект статистики
     * @return Map String, String
     */
    Map<String, String> saveStatistic(EndpointHitDto endpointHitDto);

    /**
     * Получения статистики по перечню uri и за определенный промежуток времени.
     *
     * @param start  Дата и время начала промежутка.
     * @param end    Дата и время окончания промежутка.
     * @param uris   Cписок uri. Может быть null
     * @param unique уникальные или нет ip обращений записанных в статистике
     * @return List ViewStatsDto
     */
    List<ViewStatsDto> getStatistic(String start, String end, List<String> uris, boolean unique);
}
