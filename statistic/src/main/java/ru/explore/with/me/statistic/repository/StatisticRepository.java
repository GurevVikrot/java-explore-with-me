package ru.explore.with.me.statistic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explore.with.me.statistic.model.EndpointHit;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
}
