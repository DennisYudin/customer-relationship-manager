package dev.yudin.dao.impl;

import dev.yudin.dao.TicketDAO;
import dev.yudin.entities.Ticket;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.mappers.TicketRowMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Log4j
@Repository("ticketDAO")
public class TicketDAOImpl implements TicketDAO {
    private static final String SQL_SELECT_TICKET_BY_ID = "SELECT * FROM tickets WHERE ticket_id = ?";
    private static final String SQL_SELECT_ALL_TICKETS_ORDER_BY = "SELECT * FROM tickets ORDER BY";
    private static final String SQL_SELECT_ALL_TICKETS_ORDER_BY_EVENT_NAME = "" +
            "SELECT * FROM tickets " +
            "ORDER BY event_name";
    private static final String SQL_SAVE_TICKET = "" +
            "INSERT INTO tickets " +
            "(ticket_id, event_name, unique_number, creation_date, status, user_id, event_id)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_TICKET = "" +
            "UPDATE tickets " +
            "SET event_name = ?, unique_number = ?, creation_date = ?, status = ?, user_id = ?, event_id = ? " +
            "WHERE ticket_id = ?";
    private static final String SQL_DELETE_TICKET = "DELETE FROM tickets WHERE ticket_id = ?";

    private static final String DEFAULT_SORT_BY_COLUMN_NAME = "event_name";

    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such ticket with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVETICKET_METHOD = "Error during call the method saveTicket()";
    private static final String ERROR_MESSAGE_FOR_UPDATETICKET_METHOD = "Error during call the method updateTicket()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TicketDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private TicketRowMapper ticketRowMapper;

    @Override
    public Ticket getById(long id) {
        log.debug("Call method getById() with id = " + id);

        try {
            Ticket ticket = jdbcTemplate.queryForObject(
                    SQL_SELECT_TICKET_BY_ID,
                    ticketRowMapper,
                    id
            );
            if (log.isDebugEnabled()) {
                log.debug("Ticket is " + ticket);
            }
            return ticket;
        } catch (EmptyResultDataAccessException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new DataNotFoundException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    @Override
    public List<Ticket> findAll(Pageable page) {
        log.debug("Call method findAll()");

        String sqlQuery = buildSqlQuery(page);

        try {
            List<Ticket> tickets = jdbcTemplate.query(
                    sqlQuery,
                    ticketRowMapper
            );
            if (log.isDebugEnabled()) {
                log.debug("Tickets are " + tickets);
            }
            return tickets;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    private String buildSqlQuery(Pageable pageable) {
        log.debug("Call method buildSqlQuery()");

        String query = SQL_SELECT_ALL_TICKETS_ORDER_BY_EVENT_NAME;
        if (pageable != null) {
            query = buildSqlQueryWithPageable(pageable);
        }
        log.debug("SQL query is " + query);
        return query;
    }

    private String buildSqlQueryWithPageable(Pageable pageable) {
        log.debug("Call method buildSqlQueryWithPageable()");

        Sort.Order order;
        if (pageable.getSort().isEmpty()) {
            order = Sort.Order.by(DEFAULT_SORT_BY_COLUMN_NAME);
        } else {
            order = pageable.getSort().iterator().next();
        }
        String query = collectSqlQuery(pageable, order);

        return query;
    }

    private String collectSqlQuery(Pageable pageable, Sort.Order sort) {
        log.debug("Call method collectSqlQuery()");

        String sortProperty = sort.getProperty();
        String sortDirectionName = sort.getDirection().name();
        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();

        String result = String.format(
                SQL_SELECT_ALL_TICKETS_ORDER_BY + " %1$s %2$s LIMIT %3$s OFFSET %4$d",
                sortProperty, sortDirectionName, pageSize, pageOffset);

        return result;
    }

    @Override
    public void save(Ticket ticket) {
        log.debug("Call method save() for ticket with id = " + ticket.getId());

        if (doesExist(ticket.getId())) {
            updateTicket(ticket);
        } else {
            saveTicket(ticket);
        }
    }

    public void saveTicket(Ticket ticket) {
        log.debug("Call method saveTicket() for ticket with id = " + ticket.getId());

        long id = ticket.getId();
        String eventName = ticket.getEventName();
        String uniqueNumber = ticket.getUniqueCode();
        Date date = ticket.getCreationDate();
        String status = ticket.getStatus();
        long userId = ticket.getUserId();
        long eventId = ticket.getEventId();

        try {
            jdbcTemplate.update(
                    SQL_SAVE_TICKET,
                    id, eventName, uniqueNumber, date, status, userId, eventId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVETICKET_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_SAVETICKET_METHOD, ex);
        }
    }

    public void updateTicket(Ticket ticket) {
        log.debug("Call method updateTicket() for ticket with id = " + ticket.getId());

        long id = ticket.getId();
        String eventName = ticket.getEventName();
        String uniqueNumber = ticket.getUniqueCode();
        Date date = ticket.getCreationDate();
        String status = ticket.getStatus();
        long userId = ticket.getUserId();
        long eventId = ticket.getEventId();

        try {
            jdbcTemplate.update(
                    SQL_UPDATE_TICKET,
                    eventName, uniqueNumber, date, status, userId, eventId, id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_UPDATETICKET_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_UPDATETICKET_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() for ticket with id = " + id);
        try {
            jdbcTemplate.update(
                    SQL_DELETE_TICKET,
                    id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }
}

