package dev.yudin.mappers;

import dev.yudin.entities.Event;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

        Event event = new Event();

        event.setId(rs.getLong("event_id"));
        event.setTitle(rs.getString("name"));
        event.setDate(rs.getTimestamp("date"));
        event.setPrice(rs.getInt("price"));
        event.setStatus(rs.getString("status"));
        event.setDescription(rs.getString("description"));
        event.setLocationId(rs.getLong("location_id"));

        return event;
    }
}
