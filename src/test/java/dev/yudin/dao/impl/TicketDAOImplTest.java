package dev.yudin.dao.impl;

import dev.yudin.configs.AppConfigTest;
import dev.yudin.dao.TicketDAO;
import dev.yudin.entities.Ticket;
import dev.yudin.exceptions.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfigTest.class)
@Sql(scripts = {
        "file:src/test/resources/createTables.sql",
        "file:src/test/resources/populateTables.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/cleanUpTables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WebAppConfiguration
public class TicketDAOImplTest {
    private static final String SQL_SELECT_TICKET_ID = "" +
            "SELECT ticket_id " +
            "FROM tickets " +
            "WHERE event_name = ? AND unique_number = ? AND creation_date = ? " +
            "AND status = ? AND user_id = ? AND event_id = ?";
    private static final String SQL_SELECT_ALL_TICKETS_ID = "SELECT ticket_id FROM tickets";

    @Autowired
    private TicketDAO ticketDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getById_ShouldReturnTicket_WhenInputIsExistId() throws ParseException {

        Date date = getDate("17-02-1992 20:45:00");
        Ticket expectedTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", date,
                "actual", 2000, 1000);

        Ticket actualTicket = ticketDAO.getById(3000);

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    public void getById_ShouldThrowDataNotFoundException_WhenInputIsIncorrectId() throws ParseException {

        assertThrows(DataNotFoundException.class, () -> ticketDAO.getById(-1));
    }

    @Test
    public void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageRequestWithoutSortValue()
            throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Ticket> actualTickets = ticketDAO.findAll(sortedByName);
        List<Ticket> expectedTickets = new ArrayList<>();

        Date firstCreationDate = getDate("17-02-1992 20:45:00");
        Ticket firstTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", firstCreationDate,
                "actual", 2000, 1000);

        Date secondCreationDate = getDate("18-02-1986 02:30:00");
        Ticket secondTicket = getTicket(
                3001, "Basta",
                "987654321", secondCreationDate,
                "cancelled", 2001, 1001);

        expectedTickets.add(secondTicket);
        expectedTickets.add(firstTicket);

        for (int i = 0; i < actualTickets.size(); i++) {

            Ticket actualTicket = actualTickets.get(i);
            Ticket expectedTicket = expectedTickets.get(i);

            assertEquals(expectedTicket, actualTicket);
        }
    }

    @Test
    public void findAll_ShouldReturnAllTicketsSortedByEventName_WhenPageIsNull()
            throws ParseException {

        Pageable page = null;

        List<Ticket> actualTickets = ticketDAO.findAll(page);
        List<Ticket> expectedTickets = new ArrayList<>();

        Date firstCreationDate = getDate("17-02-1992 20:45:00");
        Ticket firstTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", firstCreationDate,
                "actual", 2000, 1000);

        Date secondCreationDate = getDate("18-02-1986 02:30:00");
        Ticket secondTicket = getTicket(
                3001, "Basta",
                "987654321", secondCreationDate,
                "cancelled", 2001, 1001);

        expectedTickets.add(secondTicket);
        expectedTickets.add(firstTicket);

        for (int i = 0; i < actualTickets.size(); i++) {

            Ticket actualTicket = actualTickets.get(i);
            Ticket expectedTicket = expectedTickets.get(i);

            assertEquals(expectedTicket, actualTicket);
        }
    }

    @Test
    public void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageRequestWithSortValue()
            throws ParseException {

        Pageable sortedByID = PageRequest.of(0, 2, Sort.by("ticket_id"));

        List<Ticket> actualTickets = ticketDAO.findAll(sortedByID);
        List<Ticket> expectedTickets = new ArrayList<>();

        Date firstCreationDate = getDate("17-02-1992 20:45:00");
        Ticket firstTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", firstCreationDate,
                "actual", 2000, 1000);

        Date secondCreationDate = getDate("18-02-1986 02:30:00");
        Ticket secondTicket = getTicket(
                3001, "Basta",
                "987654321", secondCreationDate,
                "cancelled", 2001, 1001);

        expectedTickets.add(firstTicket);
        expectedTickets.add(secondTicket);

        for (int i = 0; i < actualTickets.size(); i++) {

            Ticket actualTicket = actualTickets.get(i);
            Ticket expectedTicket = expectedTickets.get(i);

            assertEquals(expectedTicket, actualTicket);
        }
    }

    @Test
    public void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageWithSizeOne()
            throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 1);

        List<Ticket> actualTickets = ticketDAO.findAll(sortedByName);
        List<Ticket> expectedTickets = new ArrayList<>();

        Date secondCreationDate = getDate("18-02-1986 02:30:00");
        Ticket secondTicket = getTicket(
                3001, "Basta",
                "987654321", secondCreationDate,
                "cancelled", 2001, 1001);

        expectedTickets.add(secondTicket);

        for (int i = 0; i < actualTickets.size(); i++) {

            Ticket actualTicket = actualTickets.get(i);
            Ticket expectedTicket = expectedTickets.get(i);

            assertEquals(expectedTicket, actualTicket);
        }
    }

    @Test
    public void save_ShouldSaveTicket_WhenInputIsTicketObjectWithDetails() throws ParseException {

        Date concertDate = getDate("17-02-1992 16:30:00");

        Ticket newTicket = getTicket(
                3002, "circus du soleil",
                "0000000000", concertDate,
                "actual", 2000, 1000
        );

        ticketDAO.save(newTicket);

        String checkName = "circus du soleil";
        String checkUniqueNumber = "0000000000";
        Date checkDate = concertDate;
        String checkStatus = "actual";
        long checkUserId = 2000;
        long checkEventId = 1000;

        long expectedId = 3002;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_TICKET_ID,
                Long.class,
                checkName, checkUniqueNumber, checkDate,
                checkStatus, checkUserId, checkEventId
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    public void save_ShouldUpdateExistedTicket_WhenInputIsTicketObjectWithDetails() throws ParseException {

        Date concertDate = getDate("01-09-2021 22:13:00");
        Ticket updatedTicket = getTicket(
                3000, "Comedy club",
                "111111111", concertDate,
                "actual", 2000, 1000
        );

        ticketDAO.save(updatedTicket);

        String checkName = "Comedy club";
        String checkUniqueNumber = "111111111";
        Date checkDate = concertDate;
        String checkStatus = "actual";
        long checkUserId = 2000;
        long checkEventId = 1000;

        long expectedId = 3000;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_TICKET_ID,
                Long.class,
                checkName, checkUniqueNumber, checkDate,
                checkStatus, checkUserId, checkEventId
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    public void delete_ShouldDeleteTicketById_WhenInputIsId() {

        long ticketId = 3000;

        ticketDAO.delete(ticketId);

        List<Long> actualId = jdbcTemplate.queryForList(
                SQL_SELECT_ALL_TICKETS_ID,
                Long.class
        );
        int expectedSize = 1;
        int actualSize = actualId.size();

        long checkedId = 3000;

        assertEquals(expectedSize, actualSize);
        assertFalse(actualId.contains(checkedId));
    }

    private Ticket getTicket(long id, String name,
                             String uniqueNumber, Date date,
                             String status, long firstForeignKey,
                             long secondForeignKey) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setEventName(name);
        ticket.setUniqueCode(uniqueNumber);
        ticket.setCreationDate(date);
        ticket.setStatus(status);
        ticket.setUserId(firstForeignKey);
        ticket.setEventId(secondForeignKey);
        return ticket;
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        Date convertedDate = simpleDateFormat.parse(date);

        return convertedDate;
    }
}

