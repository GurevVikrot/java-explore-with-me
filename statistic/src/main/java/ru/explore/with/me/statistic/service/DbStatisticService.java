package ru.explore.with.me.statistic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.dto.ViewStatsDto;
import ru.explore.with.me.statistic.exception.StatisticError;
import ru.explore.with.me.statistic.mapper.EndpointHitMapper;
import ru.explore.with.me.statistic.model.EndpointHit;
import ru.explore.with.me.statistic.repository.StatisticDao;
import ru.explore.with.me.statistic.repository.StatisticRepository;
import ru.explore.with.me.statistic.util.CustomTimeFormatter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DbStatisticService implements StatisticService{
    private final StatisticRepository repository;
    private final EndpointHitMapper mapper;
    private final StatisticDao statisticDao;

    @Autowired
    public DbStatisticService(StatisticRepository repository, EndpointHitMapper mapper, StatisticDao statisticDao) {
        this.repository = repository;
        this.mapper = mapper;
        this.statisticDao = statisticDao;
    }

    @Override
    public Map<String, String> saveStatistic(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = mapper.toEndpointHit(endpointHitDto);
        repository.save(endpointHit);
        return Map.of("Stat info:", "Информация сохранена");
    }

    @Override
    public List<ViewStatsDto> getStatistic(String startCoded, String endCoded, List<String> uris, boolean unique) {
        URLDecoder.decode(startCoded, StandardCharsets.UTF_8);
        LocalDateTime start = LocalDateTime.parse(
                URLDecoder.decode(startCoded, StandardCharsets.UTF_8), CustomTimeFormatter.getFormatter());
        LocalDateTime end = LocalDateTime.parse(
                URLDecoder.decode(endCoded, StandardCharsets.UTF_8), CustomTimeFormatter.getFormatter());

        return statisticDao.getHits(start,end,uris,unique).stream()
                .map(mapper::toViewDto)
                .collect(Collectors.toList());
    }
}
