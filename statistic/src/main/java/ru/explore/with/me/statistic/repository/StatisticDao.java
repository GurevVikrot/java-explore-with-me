package ru.explore.with.me.statistic.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.statistic.model.ViewStats;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatisticDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
//        SELECT app, uri, COUNT(DISTINCT ip)
//                FROM statistic
//        WHERE time BETWEEN 'start' AND 'end'
//        AND app = 'explore'
//        AND uri IN ('/get/event/1', '/get/event/2')
//        GROUP BY uri, app;
        StringBuilder sql = new StringBuilder("SELECT app, uri, ");

        if (unique) {
            sql.append("COUNT(DISTINCT ip) AS hits FROM statistic ");
        } else {
            sql.append("COUNT(ip) AS hits FROM statistic ");
        }

        sql.append("WHERE time BETWEEN ? and ? ");
        sql.append("AND app = 'ewm-main-service' ");

        if (uris != null && !uris.isEmpty()) {
            sql.append("AND uri IN (?) GROUP BY uri, app");
        } else {
            sql.append("GROUP BY uri, app");
        }

        return jdbcTemplate.query(sql.toString(),
                (rs, rowNum) -> new ViewStats(
                        rs.getString("app"),
                        rs.getString("uri"),
                        rs.getInt("hits")),
                start, end, uris.toArray());
    }
}
