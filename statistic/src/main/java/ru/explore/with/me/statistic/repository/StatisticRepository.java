package ru.explore.with.me.statistic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.statistic.dto.EndpointHitDto;
import ru.explore.with.me.statistic.model.EndpointHit;
import ru.explore.with.me.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
}
