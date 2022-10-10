package ru.explore.with.me.statistic.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatisticDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> stats = new ArrayList<>();

        if (uris == null) {
            return getHits(start,end, unique);
        }

        String sql = sqlHitBuilder(start, end,uris,unique);

        for (String s : uris) {
            stats.add(jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new ViewStats(
                            rs.getString("app"),
                            rs.getString("uri"),
                            rs.getInt("hits")),
                    start, end, s));
        }

        return stats;
    }

    private List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, boolean unique) {
        return jdbcTemplate.query(sqlHitBuilder(start, end, null, unique),
                (rs, rowNum) -> new ViewStats(
                        rs.getString("app"),
                        rs.getString("uri"),
                        rs.getInt("hits")),
                start, end);
    }

    public String sqlHitBuilder(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        StringBuilder sql = new StringBuilder("SELECT app, uri, ");
        boolean urisCheck = false;

        if (unique) {
            sql.append("COUNT(DISTINCT ip) AS hits FROM statistic ");
        } else {
            sql.append("COUNT(ip) AS hits FROM statistic ");
        }

        if (start != null) {
            if (end != null) {
                sql.append("WHERE time BETWEEN ? and ? ");
            } else {
                sql.append("WHERE time AFTER ? ");
            }
        } else if (end != null) {
            sql.append("WHERE time BEFORE ? ");
        } else if (uris != null && !uris.isEmpty()) {
            sql.append("WHERE uri = ? ");
            urisCheck = true;
        }

        if (uris != null && !uris.isEmpty() && !urisCheck) {
            sql.append("AND uri = ? ");
        }

        sql.append("GROUP BY uri, app");

        return sql.toString();
    }
}
