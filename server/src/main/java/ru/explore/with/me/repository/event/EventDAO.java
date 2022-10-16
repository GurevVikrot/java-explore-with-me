package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.model.category.Category;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.EventSort;
import ru.explore.with.me.util.EventStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class EventDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public EventDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для выборки событий для админского эндпоинта
     * @param users
     * @param states
     * @param categories
     * @param rangeStart
     * @param rangeEnd
     * @param from
     * @param size
     * @return
     */
    public List<Event> findAllToAdmin(
            List<Long> users,
            List<EventStatus> states,
            List<Integer> categories,
            LocalDateTime rangeStart, // Выбор событий после даты
            LocalDateTime rangeEnd, // Выбор событий до даты
            int from,
            int size) {

        List<Object> args = new ArrayList<>();
        List<Integer> argTypes = new ArrayList<>();

        StringBuilder sql = getSqlBuilder();
        sql.append("WHERE true ");

        if (users != null && !users.isEmpty()) {
            sql.append("AND e.creator = ANY(ARRAY[?]) ");
            args.add(users);
            argTypes.add(Types.BIGINT);
        }

        if (states!= null && !states.isEmpty()) {
            sql.append("AND e.status = ANY (ARRAY[?]) ");
            args.add(states);
            argTypes.add(Types.VARCHAR);
        }

        if (categories!= null && !categories.isEmpty()) {
            sql.append("AND e.category_id = ANY(ARRAY[?]) ");
            args.add(categories);
            argTypes.add(Types.INTEGER);
        }

        if (rangeStart != null) {
            if (rangeEnd != null) {
                sql.append("AND e.event_date BETWEEN ? AND ? ");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
                args.add(rangeEnd);
                argTypes.add(Types.TIMESTAMP);
            } else {
                sql.append("AND e.event_date AFTER ? ");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
            }
        } else if (rangeEnd != null) {
            sql.append("AND e.event_date BEFORE ? ");
            args.add(rangeEnd);
            argTypes.add(Types.TIMESTAMP);
        }

        sql.append("OFFSET ? LIMIT ?");
        args.add(from);
        argTypes.add(Types.INTEGER);
        args.add(size);
        argTypes.add(Types.INTEGER);

        return jdbcTemplate.query(
                sql.toString(),
                args.toArray(),
                argTypes.stream().mapToInt(Integer::intValue).toArray(),
                (rs, rowNum) -> eventFromDb(rs));
    }

    /**
     * Метод для выборки событий для публичного эндпоинта получения всех событий
     * @param text
     * @param categories
     * @param paid
     * @param rangeStart
     * @param rangeEnd
     * @param onlyAvailable
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public List<Event> findAllByFilter(String text,
                                List<Integer> categories,
                                boolean paid,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                boolean onlyAvailable,
                                EventSort sort,
                                int from,
                                int size) {
        StringBuilder sql = getSqlBuilder();
        List<Object> args = new ArrayList<>();
        List<Integer> argTypes = new ArrayList<>();

        sql.append("LEFT Outer JOIN (SELECT event_id, COUNT(user_id) AS count " +
                "FROM participants " +
                "WHERE status = 'WAITING' " +
                "GROUP BY (event_id)) AS p ON e.id = p.event_id ");
        sql.append("WHERE e.status = 'PUBLISHED' ");

        if (text != null && !text.isEmpty()) {
            sql.append("AND UPPER(e.annotation) LIKE UPPER(CONCAT('%', ?, '%')) " +
                    "OR UPPER(e.description) LIKE UPPER(CONCAT('%', ?, '%'))");
            args.add(text);
            args.add(text);
            argTypes.add(Types.VARCHAR);
            argTypes.add(Types.VARCHAR);
        }

        if (categories != null && !categories.isEmpty()) {
            sql.append("AND e.category_id = ANY(ARRAY[?]) ");
            args.add(categories);
            argTypes.add(Types.INTEGER);
        }

        if (paid) {
            sql.append("AND e.paid = true ");
        }

        if (rangeStart != null) {
            if (rangeEnd != null) {
                sql.append("AND e.event_date BETWEEN ? AND ? ");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
                args.add(rangeEnd);
                argTypes.add(Types.TIMESTAMP);
            } else {
                sql.append("AND e.event_date AFTER ? ");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
            }
        } else if (rangeEnd != null) {
            sql.append("AND e.event_date BEFORE ? ");
            args.add(rangeEnd);
            argTypes.add(Types.TIMESTAMP);
        } else { // если в запросе не указан диапазон дат [rangeStart-rangeEnd],
            // то нужно выгружать события, которые произойдут позже текущей даты и времени
            sql.append("AND e.event_date AFTER ? ");
            args.add(LocalDateTime.now());
            argTypes.add(Types.TIMESTAMP);
        }

        if (onlyAvailable) {
            sql.append("AND e.participant_limit >= p.count OR e.participant_limit = 0 OR p.count IS NULL ");
        }

        if (sort.equals(EventSort.EVENT_DATE)) { // Сортировка по дате события от ближайших по возрастанию
            sql.append("ORDER BY e.event_date ");
        }

        sql.append("OFFSET ? LIMIT ?");
        args.add(from);
        argTypes.add(Types.INTEGER);
        args.add(size);
        argTypes.add(Types.INTEGER);

        return jdbcTemplate.query(
                sql.toString(),
                args.toArray(),
                argTypes.stream().mapToInt(Integer::intValue).toArray(),
                (rs, rowNum) -> eventFromDb(rs));
    }

    private StringBuilder getSqlBuilder() {
        return new StringBuilder(
                "SELECT " +
                        "e.id as e_id, " +
                        "e.title as e_title, " +
                        "e.annotation as e_annotation, " +
                        "e.description as e_description, " +
                        "e.event_date as e_event_date, " +
                        "e.paid as e_paid, " +
                        "e.cost as e_cost, " +
                        "e.participant_limit as e_participant_limit, " +
                        "e.request_moderation as e_request_moderation, " +
                        "e.status as e_status, " +
                        "e.created as e_created, " +
                        "e.published as e_published, " +
                        "c.id as c_id, " +
                        "c.name as c_name, " +
                        "c.description as c_description, " +
                        "u.id as u_id, " +
                        "u.name as u_name, " +
                        "u.email as u_email, " +
                        "u.created as u_created " +
                        "FROM events AS e " +
                        "LEFT OUTER JOIN categories AS c on e.category_id = c.id " +
                        "LEFT OUTER JOIN users AS u ON e.creator = u.id ");
    }
    private Event eventFromDb(ResultSet rs) throws SQLException {

        return new Event(
                rs.getLong("e_id"),
                rs.getString("e_title"),
                rs.getString("e_annotation"),
                rs.getString("e_description"),
                new User(
                        rs.getLong("u_id"),
                        rs.getString("u_email"),
                        rs.getString("u_name"),
                        rs.getTimestamp("u_created").toLocalDateTime()),
                rs.getTimestamp("e_event_date").toLocalDateTime(),
                rs.getBoolean("e_paid"),
                rs.getInt("e_cost"),
                new Category(
                        rs.getInt("c_id"),
                        rs.getString("c_name"),
                        rs.getString("c_description")),
                null,
                rs.getInt("e_participant_limit"),
                rs.getBoolean("e_request_moderation"),
                EventStatus.valueOf(rs.getString("e_status")),
                rs.getTimestamp("e_created").toLocalDateTime(),
                rs.getTimestamp("e_published") == null? null : rs.getTimestamp("e_published").toLocalDateTime()
        );
    }
}
