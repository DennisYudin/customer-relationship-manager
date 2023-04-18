package dev.yudin.services.impl;

import dev.yudin.dao.UserDAO;
import dev.yudin.entities.Event;
import dev.yudin.entities.User;
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

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getById_ShouldReturnUser_WhenInputIsExistId() {

        User expectedUser = getUser(
                2000, "Dennis", "Yudin",
                "dennisYudin@mail.ru", "Big boss",
                "0000", "customer"
        );
        Mockito.when(userDAO.getById(2000)).thenReturn(expectedUser);

        User actualUser = userService.getById(2000);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getById_ShouldThrowServiceException_WhenInputIsIncorrectId() {

        Mockito.when(userDAO.getById(-1)).thenThrow(ServiceException.class);

        assertThrows(ServiceException.class, () -> userService.getById(-1));
    }

    @Test
    void findAll_ShouldReturnAllUsersSortedByName_WhenInputIsPageRequestWithoutSort() {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<User> expectedUsers = new ArrayList<>();

        User firstUser = getUser(
                2000, "Dennis",
                "Yudin", "dennisYudin@mail.ru",
                "Big boss", "0000", "customer");

        User secondUser = getUser(
                2001, "Mark",
                "Batmanov", "redDragon@mail.ru",
                "HelloWorld", "1234", "customer");

        expectedUsers.add(firstUser);
        expectedUsers.add(secondUser);

        Mockito.when(userDAO.findAll(sortedByName)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll(sortedByName);

        assertTrue(expectedUsers.containsAll(actualUsers));
    }

    @Test
    void findAll_ShouldReturnOneUserSortedByName_WhenInputIsPageWithSizeOne() {

        Pageable sortedByName = PageRequest.of(0, 1);

        List<User> expectedUsers = new ArrayList<>();

        User firstUser = getUser(
                2000, "Dennis",
                "Yudin", "dennisYudin@mail.ru",
                "Big boss", "0000", "customer");

        expectedUsers.add(firstUser);

        Mockito.when(userDAO.findAll(sortedByName)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll(sortedByName);

        assertTrue(expectedUsers.containsAll(actualUsers));
    }

    @Test
    void findAll_ShouldReturnAllUsersSortedBySurname_WhenInputIsPageRequestWithSortValue() {

        Pageable sortedBySurname = PageRequest.of(0, 2, Sort.by("surname"));

        List<User> expectedUsers = new ArrayList<>();

        User firstUser = getUser(
                2000, "Dennis",
                "Yudin", "dennisYudin@mail.ru",
                "Big boss", "0000", "customer");

        User secondUser = getUser(
                2001, "Mark",
                "Batmanov", "redDragon@mail.ru",
                "HelloWorld", "1234", "customer");

        expectedUsers.add(secondUser);
        expectedUsers.add(firstUser);

        Mockito.when(userDAO.findAll(sortedBySurname)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll(sortedBySurname);

        assertTrue(expectedUsers.containsAll(actualUsers));
    }

    @Test
    void findAll_ShouldReturnAllUsersSortedByName_WhenPageIsNull() {

        Pageable page = null;

        List<User> expectedUsers = new ArrayList<>();

        User firstUser = getUser(
                2000, "Dennis",
                "Yudin", "dennisYudin@mail.ru",
                "Big boss", "0000", "customer");

        User secondUser = getUser(
                2001, "Mark",
                "Batmanov", "redDragon@mail.ru",
                "HelloWorld", "1234", "customer");

        expectedUsers.add(firstUser);
        expectedUsers.add(secondUser);

        Mockito.when(userDAO.findAll(page)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAll(page);

        assertTrue(expectedUsers.containsAll(actualUsers));
    }

    @Test
    void save_ShouldSaveNewUser_WhenInputIsUserObjectWithDetails() {

        User newUser = getUser(
                2002, "Vandam",
                "Ivanov", "machoMan2013@yandex.ru",
                "wonderfulFlower", "1234",
                "customer"
        );
        Mockito.when(userDAO.getById(2002)).thenThrow(DataNotFoundException.class);

        userService.save(newUser);

        Mockito.verify(userDAO, Mockito.times(1)).save(newUser);
    }

    @Test
    void save_ShouldUpdateExistedUser_WhenInputIsUserObjectWithDetails() {

        User oldUser = getUser(
                2000, "Dennis",
                "Yudin", "dennisYudin@mail.ru",
                "Big boss", "0000", "customer");
        User updatedUser = getUser(
                2000, "Oleg",
                "Petrov", "machoMan2013@yandex.ru",
                "wonderfulFlower", "0000",
                "customer"
        );
        Mockito.when(userDAO.getById(200)).thenReturn(oldUser);

        userService.save(updatedUser);

        Mockito.verify(userDAO, Mockito.times(1)).save(updatedUser);
    }

    @Test
    void delete_ShouldDeleteUserById_WhenInputIsId() {

        userService.delete(2000);

        Mockito.verify(userDAO, Mockito.times(1)).delete(2000);
    }

    @Test
    void delete_ShouldThrowServiceException_WhenInputHasNegativeId() {

        assertThrows(ServiceException.class, () -> userService.delete(-1));
    }

    @Test
    void getAllEventsByUserId_ShouldReturnTwoEvents_WhenInputIsUserIdAndPageable() throws ParseException {

        Pageable pageable = PageRequest.of(0, 2);

        long userId = 2000;

        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("13-08-2021 18:23:00");
        Event firstEvent = getEvent(
                1000, "Oxxxymiron concert",
                firstConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        Date secondConcertDate = getDate("14-09-2019 15:30:00");
        Event secondEvent = getEvent(
                1001, "Basta",
                secondConcertDate, 1000,
                "actual", "Bla bla bla",
                100
        );
        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        Mockito.when(userDAO.getAllEventsByUserId(userId, pageable)).thenReturn(expectedEvents);

        List<Event> actualEvents = userDAO.getAllEventsByUserId(userId, pageable);

        assertTrue(actualEvents.containsAll(expectedEvents));
    }

    @Test
    void getAllEventsByUserId_ShouldReturnTwoEvents_WhenInputIsUserIdAndPageableIsNull() throws ParseException {

        Pageable pageable = null;

        long userId = 2000;

        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("13-08-2021 18:23:00");
        Event firstEvent = getEvent(
                1000, "Oxxxymiron concert",
                firstConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        Date secondConcertDate = getDate("14-09-2019 15:30:00");
        Event secondEvent = getEvent(
                1001, "Basta",
                secondConcertDate, 1000,
                "actual", "Bla bla bla",
                100
        );
        expectedEvents.add(firstEvent);
        expectedEvents.add(secondEvent);

        Mockito.when(userDAO.getAllEventsByUserId(userId, pageable)).thenReturn(expectedEvents);

        List<Event> actualEvents = userDAO.getAllEventsByUserId(userId, pageable);

        assertTrue(actualEvents.containsAll(expectedEvents));
    }

    @Test
    void getAllEventsByUserId_ShouldReturnOneEvent_WhenInputIsUserIdAndPageableSizeOne() throws ParseException {

        Pageable pageable = PageRequest.of(0, 1);

        long userId = 2000;

        List<Event> expectedEvents = new ArrayList<>();

        Date firstConcertDate = getDate("13-08-2021 18:23:00");
        Event firstEvent = getEvent(
                1000, "Oxxxymiron concert",
                firstConcertDate, 5000,
                "actual", "Oxxxymiron is",
                101
        );
        expectedEvents.add(firstEvent);

        Mockito.when(userDAO.getAllEventsByUserId(userId, pageable)).thenReturn(expectedEvents);

        List<Event> actualEvents = userDAO.getAllEventsByUserId(userId, pageable);

        assertTrue(actualEvents.containsAll(expectedEvents));
    }

    @Test
    void getAllEventsByUserId_ShouldReturnEmptyList_WhenInputIsDoesNotExistUserId() {

        Pageable pageable = PageRequest.of(0, 2);

        List<Event> expectedEvents = new ArrayList<>();

        Mockito.when(userDAO.getAllEventsByUserId(165, pageable)).thenReturn(expectedEvents);

        List<Event> actualEvents = userService.getAllEventsByUserId(165, pageable);

        assertTrue(actualEvents.isEmpty());
    }

    @Test
    void getAllEventsByUserId_ShouldThrowServiceException_WhenInputHasNegativeId() {

        Pageable pageable = PageRequest.of(0, 2);

        assertThrows(ServiceException.class, () -> userService.getAllEventsByUserId(-1, pageable));
    }

    @Test
    void assignEvent_ShouldAddNewEvent_WhenInputIsUserIdAndEventId() {

        long userId = 2001;
        long eventId = 1001;

        userService.assignEvent(2001, 1001);

        Mockito.verify(userDAO, Mockito.times(1)).assignEvent(userId, eventId);
    }

    @Test
    void removeEvent_ShouldDeleteEvent_WhenInputIsUserIdIdAndEventId() {

        long userId = 2000;
        long eventId = 1000;

        userService.removeEvent(userId, eventId);

        Mockito.verify(userDAO, Mockito.times(1)).removeEvent(userId, eventId);
    }

    private User getUser(long id, String name,
                         String surname, String email,
                         String login, String password,
                         String type) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(password);
        user.setType(type);
        return user;
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
}

