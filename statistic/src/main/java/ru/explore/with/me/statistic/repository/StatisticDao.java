package ru.explore.with.me.statistic.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dto класс для работы с БД и получения статистики обращений к uri
 */
@Component
@Slf4j
public class StatisticDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получение количества обращений к uri за промежуток времени.
     * uris быть null или default.
     *
     * @param start  Дата и время начала промежутка.
     * @param end    Дата и время окончания промежутка.
     * @param uris   Cписок uri. Может быть null
     * @param unique уникальные или нет ip обращений записанных в статистике
     * @return List ViewStats
     */
    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> stats = new ArrayList<>();

        if (uris == null) {
            return getHits(start, end, unique);
        }

        String sql = sqlHitBuilder(start, end, uris, unique);
        try {
            for (String s : uris) {
                stats.add(jdbcTemplate.queryForObject(sql,
                        (rs, rowNum) -> new ViewStats(
                                rs.getString("app"),
                                rs.getString("uri"),
                                rs.getInt("hits")),
                        start, end, s));
            }
        } catch (EmptyResultDataAccessException exc) {
            log.info("При поиске статистики не найдено подходящих объектов." +
                    " Сервис запрашивает отсутствующую статистику");
            return List.of();
        }

        return stats;
    }

    /**
     * Получение всей статистики за промежуток времени
     *
     * @param start  Дата и время начала промежутка.
     * @param end    Дата и время окончания промежутка.
     * @param unique уникальные или нет ip обращений записанных в статистике
     * @return List ViewStats
     */
    private List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, boolean unique) {
        return jdbcTemplate.query(sqlHitBuilder(start, end, null, unique),
                (rs, rowNum) -> new ViewStats(
                        rs.getString("app"),
                        rs.getString("uri"),
                        rs.getInt("hits")),
                start, end);
    }

    /**
     * Метод получения строки sql запроса к БД с динамическим образованием запроса
     * в зависимости от передаваемых параметров
     *
     * @param start  Дата и время начала промежутка. Может быть null
     * @param end    Дата и время окончания промежутка. Может быть null
     * @param uris   Список uri. Может быть null
     * @param unique уникальные или нет ip обращений записанных в статистике
     * @return String
     */
    private String sqlHitBuilder(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
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
