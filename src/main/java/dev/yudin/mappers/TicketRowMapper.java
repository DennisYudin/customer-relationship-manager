package dev.yudin.mappers;

import dev.yudin.entities.Ticket;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TicketRowMapper implements RowMapper<Ticket> {

    @Override
    public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {

        Ticket ticket = new Ticket();

        ticket.setId(rs.getLong("ticket_id"));
        ticket.setEventName(rs.getString("event_name"));
        ticket.setUniqueCode(rs.getString("unique_number"));
        ticket.setCreationDate(rs.getTimestamp("creation_date"));
        ticket.setStatus(rs.getString("status"));
        ticket.setUserId(rs.getLong("user_id"));
        ticket.setEventId(rs.getLong("event_id"));

        return ticket;
    }
}
