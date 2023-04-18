package dev.yudin.services.impl;

import dev.yudin.dao.LocationDAO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceImplTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationDAO locationDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getById_ShouldReturnLocation_WhenInputIsExistId() {

        Location expectedLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        Mockito.when(locationDAO.getById(100)).thenReturn(expectedLocation);

        Location actualLocation = locationService.getById(100);

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    void getById_ShouldThrowServiceException_WhenInputIsIncorrectId() {

        Mockito.when(locationDAO.getById(-1)).thenThrow(ServiceException.class);

        assertThrows(ServiceException.class, () -> locationService.getById(-1));
    }

    @Test
    void findAll_ShouldReturnAllLocationsSortedByName_WhenInputIsPageRequestWithoutSortValue() {

        Pageable sortedByName = PageRequest.of(0, 2);

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

        Mockito.when(locationDAO.findAll(sortedByName)).thenReturn(expectedLocations);

        List<Location> actualLocations = locationService.findAll(sortedByName);

        assertTrue(expectedLocations.containsAll(actualLocations));
    }

    @Test
    void findAll_ShouldReturnOneLocationSortedByName_WhenInputIsPageRequestWithSizeOneWithoutSortValue() {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Location> expectedLocations = new ArrayList<>();

        Location firstLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300);

        expectedLocations.add(firstLocation);

        Mockito.when(locationDAO.findAll(sortedByName)).thenReturn(expectedLocations);

        List<Location> actualLocations = locationService.findAll(sortedByName);

        assertTrue(expectedLocations.containsAll(actualLocations));
    }

    @Test
    void findAll_ShouldReturnAllLocationsSortedByLocationId_WhenInputIsPageRequestWithSortValue() {

        Pageable sortedById = PageRequest.of(0, 2, Sort.by("location_id"));

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

        Mockito.when(locationDAO.findAll(sortedById)).thenReturn(expectedLocations);

        List<Location> actualLocations = locationService.findAll(sortedById);

        assertTrue(expectedLocations.containsAll(actualLocations));
    }

    @Test
    void findAll_ShouldGetAllCategoriesSortedByName_WhenPageIsNull() {

        Pageable page = null;

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

        Mockito.when(locationDAO.findAll(page)).thenReturn(expectedLocations);

        List<Location> actualLocations = locationService.findAll(page);

        assertTrue(expectedLocations.containsAll(actualLocations));
    }

    @Test
    void save_ShouldSaveNewLocation_WhenInputIsNewLocationWithDetails() {

        Location newLocation = getLocation(
                102, "Green sleeve",
                "10:00-15:00", "restaurant",
                "Derzhavina str., 13", "The first Irish pub in the city",
                1200
        );
        Mockito.when(locationDAO.getById(102)).thenThrow(DataNotFoundException.class);

        locationService.save(newLocation);

        Mockito.verify(locationDAO, Mockito.times(1)).save(newLocation);
    }

    @Test
    void save_ShouldThrowServiceException_WhenInputIsHasNegativeId() {

        Location newLocation = getLocation(
                -102, "Green sleeve",
                "10:00-15:00", "restaurant",
                "Derzhavina str., 13", "The first Irish pub in the city",
                1200
        );
        assertThrows(ServiceException.class, () -> locationService.save(newLocation));
    }

    @Test
    void save_ShouldUpdateExistedLocation_WhenInputIsLocationWithDetails() {

        Location oldLocation = getLocation(
                100, "Drunk oyster",
                "08:00-22:00", "bar",
                "FooBar street", "description test", 300
        );
        Location updatedLocation = getLocation(
                100, "Green sleeve",
                "10:00-15:00", "restaurant",
                "Derzhavina str., 13", "The first Irish pub in the city",
                1200
        );
        Mockito.when(locationDAO.getById(100)).thenReturn(oldLocation);

        locationService.save(updatedLocation);

        Mockito.verify(locationDAO, Mockito.times(1)).save(updatedLocation);
    }

    @Test
    void delete_ShouldDeleteLocationById_WhenInputIsId() {

        locationService.delete(100);

        Mockito.verify(locationDAO, Mockito.times(1)).delete(100);
    }

    @Test
    void delete_ShouldThrowServiceException_WhenInputHasNegativeId() {

        assertThrows(ServiceException.class, () -> locationService.delete(-1));
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

