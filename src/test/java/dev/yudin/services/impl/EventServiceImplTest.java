package dev.yudin.services.impl;

import dev.yudin.dao.EventDAO;
import dev.yudin.dao.LocationDAO;
import dev.yudin.dto.EventDTO;
import dev.yudin.dto.mapper.EventMapper;
import dev.yudin.entities.Event;
import dev.yudin.entities.Location;
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

class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventDAO eventDAO;

    @Mock
    private LocationDAO locationDAO;

    @Mock
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getById_ShouldReturnEvent_WhenInputIsExistId() throws ParseException {

        Date date = getDate("13-08-2021 18:23:00");
        Event expectedEvent = getEvent(
                1000, "Oxxxymiron concert",
                date, 5000, "actual",
                "Oxxxymiron is", 101
        );
        Mockito.when(eventDAO.getById(1000)).thenReturn(expectedEvent);

        Event actualEvent = eventService.getById(1000);

        assertEquals(expectedEvent, actualEvent);
    }


    @Test
    void getById_ShouldThrowServiceException_WhenInputIsIncorrectId() {

        Mockito.when(eventDAO.getById(-1)).thenThrow(ServiceException.class);

        assertThrows(ServiceException.class, () -> eventService.getById(-1));
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByName_WhenInputIsPageRequestWithoutSort() throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("14-09-2019 15:30:00");
        Event firstEvent = getEvent(
                1001, "Basta",
                firstConcertDate, 1000,
                "actual", "Bla bla bla",
                100
        );
        Date secondConcertDate = getDate("13-08-2021 18:23:00");
        Event secondEvent = getEvent(
                1000, "Oxxxymiron concert",
                secondConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        Mockito.when(eventDAO.findAll(sortedByName)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.findAll(sortedByName);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByEventId_WhenInputIsPageRequestWithSort() throws ParseException {

        Pageable sortedById = PageRequest.of(0, 2, Sort.by("event_id"));

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

        Mockito.when(eventDAO.findAll(sortedById)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.findAll(sortedById);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    void findAll_ShouldReturnOneEventSortedByName_WhenInputIsPageRequestWithPageSizeOneWithoutSortValue()
            throws ParseException {

        Pageable sortedByName = PageRequest.of(0, 1);

        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("14-09-2019 15:30:00");
        Event firstEvent = getEvent(1001, "Basta",
                firstConcertDate, 1000,
                "actual", "Bla bla bla",
                100);

        expectedEvents.add(firstEvent);

        Mockito.when(eventDAO.findAll(sortedByName)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.findAll(sortedByName);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    void findAll_ShouldReturnAllEventsSortedByName_WhenPageIsNull() throws ParseException {

        Pageable page = null;

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

        Mockito.when(eventDAO.findAll(page)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.findAll(page);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    void save_ShouldSaveNewEvent_WhenInputIsNewEventObjectWithDetails() throws ParseException {

        Date concertDate = getDate("17-02-1992 16:30:00");
        Event newEvent = getEvent(
                1002, "Leonid Agutin",
                concertDate, 10000,
                "cancelled", "Wonderful concert...",
                100
        );
        Mockito.when(eventDAO.getById(1002)).thenThrow(DataNotFoundException.class);

        eventService.save(newEvent);

        Mockito.verify(eventDAO, Mockito.times(1)).save(newEvent);
    }

    @Test
    void save_ShouldUpdateExistedEvent_WhenInputIsEventObjectWithDetails() throws ParseException {

        Date concertDate = getDate("13-08-2021 18:23:00");
        Event oldEvent = getEvent(1000, "Oxxxymiron concert",
                concertDate, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        Date date = getDate("17-02-1992 16:30:00");
        Event updatedEvent = getEvent(
                1000, "Leonid Agutin",
                date, 1_000_000,
                "cancelled", "Wonderful concert...",
                100
        );
        Mockito.when(eventDAO.getById(1000)).thenReturn(oldEvent);

        eventService.save(updatedEvent);

        Mockito.verify(eventDAO, Mockito.times(1)).save(updatedEvent);
    }

    @Test
    void delete_ShouldDeleteEventById_WhenInputIsId() {

        eventService.delete(1000);

        Mockito.verify(eventDAO, Mockito.times(1)).delete(1000);
    }

    @Test
    void delete_ShouldThrowServiceException_WhenInputHasNegativeId() {

        assertThrows(ServiceException.class, () -> eventService.delete(-1000));
    }

    @Test
    void getAllCategoriesByEventId_ShouldReturnAllCategories_WhenInputIsEventId() {

        List<String> expectedCategoryNames = new ArrayList<>();

        expectedCategoryNames.add("exhibition");
        expectedCategoryNames.add("movie");
        expectedCategoryNames.add("theatre");

        Mockito.when(eventDAO.getAllCategoriesByEventId(1000)).thenReturn(expectedCategoryNames);

        List<String> actualCategoryNames = eventService.getAllCategoriesByEventId(1000);

        assertTrue(expectedCategoryNames.containsAll(actualCategoryNames));
    }

    @Test
    void getAllCategoriesByEventId_ShouldThrowServiceException_WhenInputHasNegativeId() {

        assertThrows(ServiceException.class, () -> eventService.getAllCategoriesByEventId(-1000));
    }

    @Test
    void getAllCategoriesByEventId_ShouldReturnEmptyList_WhenInputIsDoesNotExistEventId() {

        List<String> expectedCategoryNames = new ArrayList<>();

        Mockito.when(eventDAO.getAllCategoriesByEventId(125)).thenReturn(expectedCategoryNames);

        List<String> actualCategoryNames = eventService.getAllCategoriesByEventId(125);

        assertTrue(expectedCategoryNames.containsAll(actualCategoryNames));
    }

    @Test
    void addNewCategory_ShouldAddNewCategory_WhenInputIsEventIdAndCategoryId() {

        long eventId = 1001;
        long categoryId = 1;

        eventService.addNewCategory(eventId, categoryId);

        Mockito.verify(eventDAO, Mockito.times(1)).assignCategory(eventId, categoryId);
    }

    @Test
    void removeCategory_ShouldDeleteCategory_WhenInputIsEventIdAndCategoryId() {

        long eventId = 1000;
        long categoryId = 1;

        eventService.removeCategory(eventId, categoryId);

        Mockito.verify(eventDAO, Mockito.times(1)).removeCategory(eventId, categoryId);
    }

    @Test
    void getEventWithDetails_ShouldReturnEventWithDetails_WhenInputIsEventId() throws ParseException {

        Date date = getDate("13-08-2021 18:23:00");
        Event event = getEvent(1000, "Oxxxymiron concert",
                date, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        Location location = getLocation(
                101, "Moes", "06:00-00:00",
                "tavern", "the great street",
                "description bla bla bla for test", 750);

        List<String> categories = new ArrayList<>();

        categories.add("exhibition");
        categories.add("movie");
        categories.add("theatre");

        Date concertDate = getDate("13-08-2021 18:23:00");

        EventDTO expectedEventDTO = new EventDTO.Builder()
                .id(1000)
                .name("Oxxxymiron concert")
                .date(concertDate)
                .price(5000)
                .eventStatus("actual")
                .eventDescription("Oxxxymiron is")
                .eventCategories(categories)
                .locationName("Moes")
                .locationWorkingHours("06:00-00:00")
                .locationType("tavern")
                .locationAddress("the great street")
                .locationDescription("description bla bla bla for test")
                .capacityPeople(750)
                .build();

        Mockito.when(eventDAO.getById(1000)).thenReturn(event);

        Mockito.when(eventDAO.getAllCategoriesByEventId(1000)).thenReturn(categories);

        Mockito.when(locationDAO.getById(101)).thenReturn(location);

        Mockito.when(eventMapper.convertToDTO(event, categories, location)).thenReturn(expectedEventDTO);

        EventDTO actualEventDTO = eventService.getEventWithDetails(1000);

        assertEquals(expectedEventDTO, actualEventDTO);
    }

    @Test
    void getEventWithDetails_ShouldThrowServiceException_WhenInputHasNegativeId() throws ParseException {

        assertThrows(ServiceException.class, () -> eventService.getEventWithDetails(-1000));
    }

    @Test
    void getEventWithDetails_ShouldThrowServiceException_WhenInputIsDoesNotExistId() throws ParseException {

        Mockito.when(eventDAO.getById(125)).thenThrow(DataNotFoundException.class);

        assertThrows(ServiceException.class, () -> eventService.getEventWithDetails(125));
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        Date convertedDate = simpleDateFormat.parse(date);

        return convertedDate;
    }

    private Location getLocation(long id, String name,
                                 String workingHours, String type,
                                 String address, String desc,
                                 int capacityPeople) {
        Location location = new Location();
        location.setId(id);
        location.setTitle(name);
        location.setWorkingHours(workingHours);
        location.setType(type);
        location.setAddress(address);
        location.setDescription(desc);
        location.setCapacityPeople(capacityPeople);
        return location;
    }
}

