package dev.yudin.dao.impl;

import dev.yudin.dao.LocationDAO;
import dev.yudin.entities.Location;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.mappers.LocationRowMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j
@Repository("locationDAO")
public class LocationDAOImpl implements LocationDAO {
    private static final String SQL_SELECT_LOCATION_BY_ID = "SELECT * FROM locations WHERE location_id = ?";
    private static final String SQL_SELECT_ALL_LOCATIONS_ORDER_BY = "SELECT * FROM locations ORDER BY";
    private static final String SQL_SELECT_ALL_LOCATIONS_ORDER_BY_NAME = "SELECT * FROM locations ORDER BY name";
    private static final String SQL_SAVE_LOCATION = "" +
            "INSERT INTO locations " +
            "(location_id, name, working_hours, type, address, description, capacity_people)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_LOCATION = "" +
            "UPDATE locations " +
            "SET name = ?, working_hours = ?, type = ?, address = ?, description = ?, capacity_people = ? " +
            "WHERE location_id = ?";
    private static final String SQL_DELETE_LOCATION = "" +
            "DELETE FROM locations " +
            "WHERE location_id = ?";

    private static final String DEFAULT_SORT_BY_COLUMN_NAME = "name";

    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such location with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVELOCATION_METHOD = "Error during call the method saveLocation()";
    private static final String ERROR_MESSAGE_FOR_UPDATELOCATION_METHOD = "Error during call the method updateLocation()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private LocationRowMapper locationRowMapper;

    @Override
    public Location getById(long id) {
        log.debug("Call method getById() with id = " + id);

        try {
            Location location = jdbcTemplate.queryForObject(
                    SQL_SELECT_LOCATION_BY_ID,
                    locationRowMapper,
                    id
            );
            if (log.isDebugEnabled()) {
                log.debug("Location is " + location);
            }
            return location;
        } catch (EmptyResultDataAccessException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new DataNotFoundException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    @Override
    public List<Location> findAll(Pageable page) {
        log.debug("Call method findAll()");

        String sqlQuery = buildSqlQuery(page);

        try {
            List<Location> locations = jdbcTemplate.query(
                    sqlQuery,
                    locationRowMapper
            );
            if (log.isDebugEnabled()) {
                log.debug("Locations are " + locations);
            }
            return locations;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    private String buildSqlQuery(Pageable pageable) {
        log.debug("Call method buildSqlQuery()");

        String query = SQL_SELECT_ALL_LOCATIONS_ORDER_BY_NAME;
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
                SQL_SELECT_ALL_LOCATIONS_ORDER_BY + " %1$s %2$s LIMIT %3$s OFFSET %4$d",
                sortProperty, sortDirectionName, pageSize, pageOffset);

        return result;
    }

    @Override
    public void save(Location location) {
        log.debug("Call method save() for location with id = " + location.getId());

        if (doesExist(location.getId())) {
            updateLocation(location);
        } else {
            saveLocation(location);
        }
    }

    public void saveLocation(Location location) {
        log.debug("Call method saveLocation() for location with id = " + location.getId());

        long id = location.getId();
        String name = location.getTitle();
        String workingHours = location.getWorkingHours();
        String type = location.getType();
        String address = location.getAddress();
        String description = location.getDescription();
        int capacityPeople = location.getCapacityPeople();

        try {
            jdbcTemplate.update(
                    SQL_SAVE_LOCATION,
                    id, name, workingHours, type, address, description, capacityPeople
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVELOCATION_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_SAVELOCATION_METHOD, ex);
        }
    }


    public void updateLocation(Location location) {
        log.debug("Call method updateLocation() for location with id = " + location.getId());

        long id = location.getId();
        String name = location.getTitle();
        String workingHours = location.getWorkingHours();
        String type = location.getType();
        String address = location.getAddress();
        String description = location.getDescription();
        int capacityPeople = location.getCapacityPeople();

        try {
            jdbcTemplate.update(
                    SQL_UPDATE_LOCATION,
                    name, workingHours, type, address, description, capacityPeople, id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_UPDATELOCATION_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_UPDATELOCATION_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() for location with id = " + id);
        try {
            jdbcTemplate.update(
                    SQL_DELETE_LOCATION,
                    id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }
}

