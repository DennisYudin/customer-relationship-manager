package dev.yudin.dao.impl;

import dev.yudin.configs.AppConfigTest;
import dev.yudin.dao.EventDAO;
import dev.yudin.entities.Event;
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
        "file:src/test/resources/populateTablesWithoutTicketTable.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/cleanUpTables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WebAppConfiguration
class EventDAOImplTest {
    private static final String SQL_SELECT_EVENT_ID = "" +
            "SELECT event_id " +
            "FROM events " +
            "WHERE name = ? AND date = ? AND price = ? AND status = ? AND description = ? AND location_id = ?";
    private static final String SQL_SELECT_ALL_EVENTS_ID = "SELECT event_id FROM events";
    private static final String SQL_SELECT_ALL_CATEGORIES_BY_EVENT_ID = "" +
            "SELECT category_id FROM events_categories " +
            "WHERE event_id = ?";
    private static final String SQL_SELECT_CATEGORY_BY_EVENT_ID_AND_CATEGORY_ID = "" +
            "SELECT category_id " +
            "FROM events_categories " +
            "WHERE event_id = ? AND category_id = ?";

    private EventDAO eventDAO;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    EventDAOImplTest(EventDAO eventDAO, JdbcTemplate jdbcTemplate) {
        this.eventDAO = eventDAO;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void getById_ShouldReturnEvent_WhenInputIsExistIdValue() throws ParseException {

        Date date = getDate("13-08-2021 18:23:00");
        Event expectedEvent = getEvent(
                1000, "Oxxxymiron concert",
                date, 5000, "actual",
                "Oxxxymiron is", 101);

        Event actualEvent = eventDAO.getById(1000);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    void getById_ShouldThrowDataNotFoundException_WhenInputIsDoesNotExistId() {

        assertThrows(DataNotFoundException.class, () -> eventDAO.getById(-1000));
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByName_WhenInputIsPageRequestWithoutSort() throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Event> actualEvents = eventDAO.findAll(sortedByName);
        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("14-09-2019 15:30:00");
        Event firstEvent = getEvent(
                1001, "Basta",
                firstConcertDate, 1000,
                "actual", "Bla bla bla",
                100);

        Date secondConcertDate = getDate("13-08-2021 18:23:00");
        Event secondEvent = getEvent(
                1000, "Oxxxymiron concert",
                secondConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101);

        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        for (int i = 0; i < actualEvents.size(); i++) {

            Event actualEvent = actualEvents.get(i);
            Event expectedEvent = expectedEvents.get(i);

            assertEquals(expectedEvent, actualEvent);
        }
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByEventId_WhenInputIsPageRequestWithSort() throws ParseException {

        Pageable sortedById = PageRequest.of(0, 2, Sort.by("event_id"));

        List<Event> actualEvents = eventDAO.findAll(sortedById);
        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("13-08-2021 18:23:00");
        Event firstEvent = getEvent(1000, "Oxxxymiron concert",
                firstConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101);

        Date secondConcertDate = getDate("14-09-2019 15:30:00");
        Event secondEvent = getEvent(1001, "Basta",
                secondConcertDate, 1000,
                "actual", "Bla bla bla",
                100);

        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        for (int i = 0; i < actualEvents.size(); i++) {

            Event actualEvent = actualEvents.get(i);
            Event expectedEvent = expectedEvents.get(i);

            assertEquals(expectedEvent, actualEvent);
        }
    }

    @Test
    void findAll_ShouldReturnOneEventSortedByName_WhenInputIsPageRequestWithPageSizeOneWithoutSortValue()
            throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 1);

        List<Event> actualEvents = eventDAO.findAll(sortedByName);
        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("14-09-2019 15:30:00");
        Event firstEvent = getEvent(1001, "Basta",
                firstConcertDate, 1000,
                "actual", "Bla bla bla",
                100);

        expectedEvents.add(firstEvent);

        for (int i = 0; i < actualEvents.size(); i++) {

            Event actualEvent = actualEvents.get(i);
            Event expectedEvent = expectedEvents.get(i);

            assertEquals(expectedEvent, actualEvent);
        }
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByName_WhenPageIsNull() throws ParseException {

        List<Event> actualEvents = eventDAO.findAll(null);
        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("14-09-2019 15:30:00");
        Event firstEvent = getEvent(1001, "Basta",
                firstConcertDate, 1000,
                "actual", "Bla bla bla",
                100);

        Date secondConcertDate = getDate("13-08-2021 18:23:00");
        Event secondEvent = getEvent(1000, "Oxxxymiron concert",
                secondConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101);

        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        for (int i = 0; i < actualEvents.size(); i++) {

            Event actualEvent = actualEvents.get(i);
            Event expectedEvent = expectedEvents.get(i);

            assertEquals(expectedEvent, actualEvent);
        }
    }

    @Test
    void save_ShouldSaveEvent_WhenInputIsEventObjectWithDetails() throws ParseException {

        Date concertDate = getDate("17-02-1992 16:30:00");
        Event event = getEvent(
                1002, "Leonid Agutin",
                concertDate, 10000,
                "cancelled", "Wonderful concert...",
                100
        );

        eventDAO.save(event);

        String checkName = "Leonid Agutin";
        int checkPrice = 10000;
        String checkStatus = "cancelled";
        String checkDesc = "Wonderful concert...";
        long checkForeignKey = 100;

        long expectedId = 1002;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_EVENT_ID,
                Long.class,
                checkName, concertDate, checkPrice,
                checkStatus, checkDesc, checkForeignKey
        );
        assertEquals(expectedId, actualId);
    }


    @Test
    void save_ShouldUpdateExistedEvent_WhenInputIsEventObjectWithDetails() throws ParseException {


        Date date = getDate("17-02-1992 16:30:00");
        Event updatedEvent = getEvent(
                1000, "Leonid Agutin",
                date, 1_000_000,
                "cancelled", "Wonderful concert...",
                100
        );

        eventDAO.save(updatedEvent);

        String checkName = "Leonid Agutin";
        int checkPrice = 1_000_000;
        String checkStatus = "cancelled";
        String checkDesc = "Wonderful concert...";
        long checkForeignKey = 100;

        long expectedId = 1000;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_EVENT_ID,
                Long.class,
                checkName, date, checkPrice,
                        checkStatus, checkDesc, checkForeignKey
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    void delete_ShouldDeleteEventById_WhenInputIsId() {

        long eventId = 1000;

        eventDAO.delete(eventId);

        List<Long> actualId = jdbcTemplate.queryForList(
                SQL_SELECT_ALL_EVENTS_ID,
                Long.class
        );

        int expectedSize = 1;
        int actualSize = actualId.size();

        Long checkedId = 1000L;

        assertEquals(expectedSize, actualSize);
        assertFalse(actualId.contains(checkedId));
    }

    @Test
    void getAllCategoriesByEventId_ShouldReturnAllCategories_WhenInputIsEventId() {

        long id = 1000;

        List<String> actualCategoryNames = eventDAO.getAllCategoriesByEventId(id);
        List<String> expectedCategoryNames = new ArrayList<>();

        expectedCategoryNames.add("exhibition");
        expectedCategoryNames.add("movie");
        expectedCategoryNames.add("theatre");

        int expectedSize = expectedCategoryNames.size();
        int actualSize = actualCategoryNames.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void assignCategory_ShouldAddNewCategory_WhenInputIsEventIdAndCategoryId() {

        long eventId = 1001;
        long categoryId = 1;

        eventDAO.assignCategory(eventId, categoryId);

        long checkEventId = 1001;
        long checkCategoryId = 1;

        long expectedId = 1;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_CATEGORY_BY_EVENT_ID_AND_CATEGORY_ID,
                Long.class,
                checkEventId, checkCategoryId
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    void removeCategory_ShouldDeleteCategory_WhenInputIsEventIdAndCategoryId() {

        long eventId = 1000;
        long categoryId = 1;

        eventDAO.removeCategory(eventId, categoryId);

        List<Long> actualCategories = jdbcTemplate.queryForList(
                SQL_SELECT_ALL_CATEGORIES_BY_EVENT_ID,
                Long.class,
                eventId
        );
        int expectedSize = 2;
        int actualSize = actualCategories.size();

        assertEquals(expectedSize, actualSize);
        assertFalse(actualCategories.contains(categoryId));
    }

    private Event getEvent(long id, String name,
                           Date date, int price,
                           String status, String desc,
                           long foreignKey) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(name);
        event.setDate(date);
        event.setPrice(price);
        event.setStatus(status);
        event.setDescription(desc);
        event.setLocationId(foreignKey);
        return event;
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return dateFormat.parse(date);
    }
}
