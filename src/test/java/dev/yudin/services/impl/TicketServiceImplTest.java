package dev.yudin.services.impl;

import dev.yudin.dao.TicketDAO;
import dev.yudin.entities.Ticket;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.exceptions.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getById_ShouldReturnTicket_WhenInputIsExistId() throws ParseException {

        Date date = getDate("17-02-1992 20:45:00");
        Ticket expectedTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", date,
                "actual", 2000, 1000);

        Mockito.when(ticketDAO.getById(3000)).thenReturn(expectedTicket);

        Ticket actualTicket = ticketService.getById(3000);

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    void getById_ShouldThrowServiceException_WhenInputIsIncorrectId() {

        Mockito.when(ticketDAO.getById(-1)).thenThrow(ServiceException.class);

        assertThrows(ServiceException.class, () -> ticketService.getById(-1));
    }

    @Test
    void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageRequestWithoutSortValue()
            throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 2);

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

        Mockito.when(ticketDAO.findAll(sortedByName)).thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.findAll(sortedByName);

        assertTrue(expectedTickets.containsAll(actualTickets));
    }

    @Test
    void findAll_ShouldReturnAllTicketsSortedByEventName_WhenPageIsNull() throws ParseException {

        Pageable page = null;

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

        Mockito.when(ticketDAO.findAll(page)).thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.findAll(page);

        assertTrue(expectedTickets.containsAll(actualTickets));
    }

    @Test
    void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageRequestWithSortValue()
            throws ParseException {

        Pageable sortedByID = PageRequest.of(0, 2, Sort.by("ticket_id"));

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

        Mockito.when(ticketDAO.findAll(sortedByID)).thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.findAll(sortedByID);

        assertTrue(expectedTickets.containsAll(actualTickets));
    }

    @Test
    void findAll_ShouldReturnAllTicketsSortedByEventName_WhenInputIsPageWithSizeOne() throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 1);

        List<Ticket> expectedTickets = new ArrayList<>();

        Date secondCreationDate = getDate("18-02-1986 02:30:00");
        Ticket secondTicket = getTicket(
                3001, "Basta",
                "987654321", secondCreationDate,
                "cancelled", 2001, 1001);

        expectedTickets.add(secondTicket);

        Mockito.when(ticketDAO.findAll(sortedByName)).thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.findAll(sortedByName);

        assertTrue(expectedTickets.containsAll(actualTickets));
    }

    @Test
    void save_ShouldSaveNewTicket_WhenInputIsNewTicketObjectWithDetails() throws ParseException {

        Date concertDate = getDate("17-02-1992 16:30:00");
        Ticket newTicket = getTicket(
                3002, "circus du soleil",
                "0000000000", concertDate,
                "actual", 2000, 1000
        );
        Mockito.when(ticketDAO.getById(3002)).thenThrow(DataNotFoundException.class);

        ticketService.save(newTicket);

        Mockito.verify(ticketDAO, Mockito.times(1)).save(newTicket);
    }

    @Test
    void save_ShouldThrowServiceException_WhenInputIsHasNegativeId() throws ParseException {

        Date concertDate = getDate("17-02-1992 16:30:00");
        Ticket newTicket = getTicket(
                -1, "circus du soleil",
                "0000000000", concertDate,
                "actual", 2000, 1000
        );
        Throwable exception = assertThrows(ServiceException.class,
                () -> ticketService.save(newTicket));

        String expected = "id can not be less or equals zero";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void save_ShouldUpdateExistedTicket_WhenInputIsTicketObjectWithDetails() throws ParseException {

        Date firstCreationDate = getDate("17-02-1992 20:45:00");
        Ticket oldTicket = getTicket(
                3000, "Oxxxymiron concert",
                "123456789", firstCreationDate,
                "actual", 2000, 1000);

        Date concertDate = getDate("01-09-2021 22:13:00");
        Ticket updatedTicket = getTicket(
                3000, "Comedy club",
                "111111111", concertDate,
                "actual", 2000, 1000
        );
        Mockito.when(ticketDAO.getById(3000)).thenReturn(oldTicket);

        ticketService.save(updatedTicket);

        Mockito.verify(ticketDAO, Mockito.times(1)).save(updatedTicket);
    }

    @Test
    void delete_ShouldDeleteCategoryById_WhenInputIsId() {

        ticketService.delete(3000);

        Mockito.verify(ticketDAO, Mockito.times(1)).delete(3000);
    }

    @Test
    void delete_ShouldThrowServiceException_WhenInputHasNegativeId() {

        assertThrows(ServiceException.class, () -> ticketService.delete(-3000));
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

