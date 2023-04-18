package dev.yudin.dao.impl;

import dev.yudin.configs.AppConfigTest;
import dev.yudin.dao.LocationDAO;
import dev.yudin.entities.Location;
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

import java.util.ArrayList;
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
public class LocationDAOImplTest {
    private static final String SQL_SELECT_LOCATION_ID = "" +
            "SELECT location_id " +
            "FROM locations " +
            "WHERE name = ? AND working_hours = ? AND type = ? AND address = ? AND description = ? AND capacity_people = ?";
    private static final String SQL_SELECT_ALL_LOCATIONS_ID = "SELECT location_id FROM locations";

    @Autowired
    private LocationDAO locationDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getById_ShouldReturnLocation_WhenInputIsExistId() {

        Location expectedLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        Location actualLocation = locationDAO.getById(100);

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void getById_ShouldThrowDataNotFoundException_WhenInputIsIncorrectId() {

        assertThrows(DataNotFoundException.class, () -> locationDAO.getById(-1));
    }

    @Test
    public void findAll_ShouldReturnAllLocationsSortedByName_WhenInputIsPageRequestWithoutSort() {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Location> actualLocations = locationDAO.findAll(sortedByName);
        List<Location> expectedLocations = new ArrayList<>();

        Location firstLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        Location secondLocation = getLocation(
                101, "Moes", "06:00-00:00",
                "tavern", "the great street",
                "description bla bla bla for test", 750);

        expectedLocations.add(firstLocation);
        expectedLocations.add(secondLocation);

        for (int i = 0; i < actualLocations.size(); i++) {

            Location actualLocation = actualLocations.get(i);
            Location expectedLocation = expectedLocations.get(i);

            assertEquals(expectedLocation, actualLocation);
        }
    }

    @Test
    public void findAll_ShouldReturnAllLocationsSortedByLocationId_WhenInputIsPageRequestWithSortValue() {

        Pageable sortedById = PageRequest.of(0, 2, Sort.by("location_id"));

        List<Location> actualLocations = locationDAO.findAll(sortedById);
        List<Location> expectedLocations = new ArrayList<>();

        Location firstLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        Location secondLocation = getLocation(
                101, "Moes", "06:00-00:00",
                "tavern", "the great street",
                "description bla bla bla for test", 750);

        expectedLocations.add(firstLocation);
        expectedLocations.add(secondLocation);

        for (int i = 0; i < actualLocations.size(); i++) {

            Location actualLocation = actualLocations.get(i);
            Location expectedLocation = expectedLocations.get(i);

            assertEquals(expectedLocation, actualLocation);
        }
    }

    @Test
    public void findAll_ShouldReturnAllEventsSortedByName_WhenPageIsNull() {

        Pageable page = null;

        List<Location> actualLocations = locationDAO.findAll(page);
        List<Location> expectedLocations = new ArrayList<>();

        Location firstLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        Location secondLocation = getLocation(
                101, "Moes", "06:00-00:00",
                "tavern", "the great street",
                "description bla bla bla for test", 750);

        expectedLocations.add(firstLocation);
        expectedLocations.add(secondLocation);

        for (int i = 0; i < actualLocations.size(); i++) {

            Location actualLocation = actualLocations.get(i);
            Location expectedLocation = expectedLocations.get(i);

            assertEquals(expectedLocation, actualLocation);
        }
    }

    @Test
    public void findAll_ShouldReturnOneLocationSortedByName_WhenInputIsPageWithSizeOne() {

        Pageable sortedById = PageRequest.of(0, 1);

        List<Location> actualLocations = locationDAO.findAll(sortedById);
        List<Location> expectedLocations = new ArrayList<>();

        Location firstLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        expectedLocations.add(firstLocation);

        for (int i = 0; i < actualLocations.size(); i++) {

            Location actualLocation = actualLocations.get(i);
            Location expectedLocation = expectedLocations.get(i);

            assertEquals(expectedLocation, actualLocation);
        }
    }

    @Test
    public void save_ShouldSaveLocation_WhenInputIsLocationObjectWithDetails() {

        Location newLocation = getLocation(
                102, "Green sleeve",
                "10:00-15:00", "restaurant",
                "Derzhavina str., 13", "The first Irish pub in the city",
                1200
        );

        locationDAO.save(newLocation);

        String checkName = "Green sleeve";
        String checkWorkingHours = "10:00-15:00";
        String checkType = "restaurant";
        String checkAddress = "Derzhavina str., 13";
        String checkDescription = "The first Irish pub in the city";
        int checkCapacityPeople = 1200;

        long expectedId = 102;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_LOCATION_ID,
                Long.class,
                checkName, checkWorkingHours, checkType,
                checkAddress, checkDescription, checkCapacityPeople
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    public void save_ShouldUpdateExistedLocation_WhenInputIsLocationObjectWithDetails() {

        Location updatedLocation = getLocation(
                100, "Green sleeve",
                "10:00-15:00", "restaurant",
                "Derzhavina str., 13", "The first Irish pub in the city",
                1200
        );

        locationDAO.save(updatedLocation);

        String checkName = "Green sleeve";
        String checkWorkingHours = "10:00-15:00";
        String checkType = "restaurant";
        String checkAddress = "Derzhavina str., 13";
        String checkDescription = "The first Irish pub in the city";
        int checkCapacityPeople = 1200;

        long expectedId = 100;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_LOCATION_ID,
                Long.class,
                checkName, checkWorkingHours, checkType,
                checkAddress, checkDescription, checkCapacityPeople
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    public void delete_ShouldDeleteLocationById_WhenInputIsId() {

        long locationId = 100;

        locationDAO.delete(locationId);

        List<Long> actualId = jdbcTemplate.queryForList(
                SQL_SELECT_ALL_LOCATIONS_ID,
                Long.class
        );
        int expectedSize = 1;
        int actualSize = actualId.size();

        int checkedId = 100;

        assertEquals(expectedSize, actualSize);
        assertFalse(actualId.contains(checkedId));
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

