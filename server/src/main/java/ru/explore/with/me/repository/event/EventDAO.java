package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.model.category.Category;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.EventStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public EventDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Event> findAllToAdmin(
            List<Long> users,
            List<EventStatus> states,
            List<Integer> categories,
            LocalDateTime rangeStart, // Выбор событий после даты
            LocalDateTime rangeEnd, // Выбор событий до даты
            int from,
            int size) {
//        SELECT *
//                FROM events
//        LEFT OUTER JOIN categories as c on events.category_id = c.id
//        LEFT OUTER JOIN users as u ON events.creator = u.id
//        WHERE category_id = ANY (ARRAY[category_id])
        List<Object> args = new ArrayList<>();
        List<Integer> argTypes = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
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
                        "u.created as u_created" +
                " FROM events AS e");

        sql.append(" LEFT OUTER JOIN categories AS c on e.category_id = c.id");
        sql.append(" LEFT OUTER JOIN users AS u ON e.creator = u.id");
        sql.append(" WHERE true");

        if (users != null && !users.isEmpty()) {
            sql.append(" AND e.creator = ANY(ARRAY[?])");
            args.add(users);
            argTypes.add(Types.BIGINT);
        }

        if (states!= null && !states.isEmpty()) {
            sql.append(" AND e.status = ANY (ARRAY[?])");
            args.add(states);
            argTypes.add(Types.VARCHAR);
        }

        if (categories!= null && !categories.isEmpty()) {
            sql.append(" AND e.category_id = ANY(ARRAY[?])");
            args.add(categories);
            argTypes.add(Types.INTEGER);
        }

        if (rangeStart != null) {
            if (rangeEnd != null) {
                sql.append( " AND e.event_date BETWEEN ? AND ?");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
                args.add(rangeEnd);
                argTypes.add(Types.TIMESTAMP);
            } else {
                sql.append(" AND e.event_date AFTER ?");
                args.add(rangeStart);
                argTypes.add(Types.TIMESTAMP);
            }
        } else if (rangeEnd != null) {
            sql.append(" AND e.event_date BEFORE ?");
            args.add(rangeEnd);
            argTypes.add(Types.TIMESTAMP);
        }

        sql.append(" OFFSET ? LIMIT ?");
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
