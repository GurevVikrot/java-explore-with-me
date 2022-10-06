package ru.explore.with.me.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.util.EventStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
        StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT * FROM events");
        sql.append(" INNER JOIN categories_events ON events.id = categories_events.event_id");
        sql.append(" INNER JOIN categories ON categories_events.category_id = categories.id");
        if (!categories.isEmpty()) {
            sql.append(" WHERE categories.id IN ?");
        }
        if (!users.isEmpty()) {
            sql.append(" AND events.creator IN ?");
        }
        if (!states.isEmpty()) {
            sql.append(" AND events.state IN ?");
        }
        if (rangeStart != null) {
            if (rangeEnd != null) {
                sql.append( " AND events.start BETWEEN ? AND ?");
            } else {
                sql.append(" AND events.start AFTER ?");
            }
        } else if (rangeEnd != null) {
            sql.append(" AND events.start BEFORE ?");
        }

        sql.append(" OFFSET ? LIMIT ?");
        sql.append(") as events_select");
        sql.append(" INNER JOIN users ON events_select.creator = users.id");

        return null;
        //return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> eventFromDb(rs), categories);
    }

//    private Event eventFromDb(ResultSet rs) throws SQLException {
//        Event event = new Event(
//                rs.getLong("events.id"),
//                rs.getString("events.title"),
//                rs.getString("events.annotation"),
//                rs.getString("events.description"),
//                new User(
//                        rs.getLong("users.id"),
//                        rs.getString("users.email"),
//                        rs.getString("users.name"),
//                        rs.getTimestamp("users.created").toLocalDateTime(),
//                        rs.getDate("users.birthday").toLocalDate()),
//                rs.getTimestamp("events.eventDate").toLocalDateTime(),
//                rs.getBoolean("paid"),
//                rs.getInt("cost"),
//                null,
//                rs.getInt("participantsLimit"),
//                rs.getBoolean("requestModeration"),
//                rs.getObject("state", EventStatus.class)
//        );
//        return event;
//    }
}
