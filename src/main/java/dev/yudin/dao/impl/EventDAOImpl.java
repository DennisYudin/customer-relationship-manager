package dev.yudin.dao.impl;

import dev.yudin.dao.EventDAO;
import dev.yudin.entities.Event;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.mappers.EventRowMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j
@Repository("eventDAO")
public class EventDAOImpl implements EventDAO {
    private static final String SQL_SELECT_EVENT_BY_ID = "SELECT * FROM events WHERE event_id = ?";
    private static final String SQL_SELECT_ALL_EVENTS_ORDER_BY = "SELECT * FROM events ORDER BY";
    private static final String SQL_SELECT_ALL_EVENTS_ORDER_BY_NAME = "SELECT * FROM events ORDER BY name";
    private static final String SQL_SAVE_EVENT = "" +
            "INSERT INTO events " +
            "(event_id, name, date, price, status, description, location_id)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_EVENT = "" +
            "UPDATE events " +
            "SET name = ?, date = ?, price = ?, status = ?, description = ?, location_id = ? " +
            "WHERE event_id = ?";
    private static final String SQL_DELETE_EVENT = "DELETE FROM events WHERE event_id = ?";
    private static final String SQL_SELECT_ALL_CATEGORIES_BY_EVENT_ID = "" +
            "SELECT category_id " +
            "FROM events_categories " +
            "WHERE event_id = ?";
    private static final String SQL_ADD_NEW_CATEGORY = "" +
            "INSERT INTO events_categories (event_id, category_id) " +
            "VALUES (?, ?)";
    private static final String SQL_DELETE_CATEGORY = "" +
            "DELETE " +
            "FROM events_categories " +
            "WHERE event_id = ? AND category_id = ?";
    private static final String SQL_SELECT_CATEGORY_NAME_BY_ID = "" +
            "SELECT name " +
            "FROM categories " +
            "WHERE category_id = ?";

    private static final String DEFAULT_SORT_BY_COLUMN_NAME = "name";

    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such event with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVEEVENT_METHOD = "Error during call the method saveEvent()";
    private static final String ERROR_MESSAGE_FOR_UPDATE_EVENT_METHOD = "Error during call the method updateEvent()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";
    private static final String ERROR_MESSAGE_FOR_CONVERTTONAMES_METHOD = "Error during call the method convertToNames()";
    private static final String ERROR_MESSAGE_FOR_GETCATEGORYIDS_METHOD = "Error during call the method getCategoryIDs()";
    private static final String ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD = "Error during call the method addNewCategory()";
    private static final String ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD = "Error during call the method removeCategory()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private EventRowMapper eventRowMapper;

    @Override
    public Event getById(long id) {
        log.debug("Call method getById() with id = " + id);

        try {
            Event event = jdbcTemplate.queryForObject(
                    SQL_SELECT_EVENT_BY_ID,
                    eventRowMapper,
                    id
            );
            if (log.isDebugEnabled()) {
                log.debug("Event is " + event);
            }
            return event;
        } catch (EmptyResultDataAccessException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new DataNotFoundException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    @Override
    public List<Event> findAll(Pageable page) {
        log.debug("Call method findAll()");

        String sqlQuery = buildSqlQuery(page);

        try {
            List<Event> events = jdbcTemplate.query(
                    sqlQuery,
                    eventRowMapper
            );
            if (log.isDebugEnabled()) {
                log.debug("Events are " + events);
            }
            return events;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    private String buildSqlQuery(Pageable pageable) {
        log.debug("Call method buildSqlQuery()");

        String query = SQL_SELECT_ALL_EVENTS_ORDER_BY_NAME;
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
                SQL_SELECT_ALL_EVENTS_ORDER_BY + " %1$s %2$s LIMIT %3$s OFFSET %4$d",
                sortProperty, sortDirectionName, pageSize, pageOffset);

        return result;
    }

    @Override
    public void save(Event event) {
        log.debug("Call method save() for event with id = " + event.getId());

        if (doesExist(event.getId())) {
            updateEvent(event);
        } else {
            saveEvent(event);
        }
    }

    public void saveEvent(Event event) {
        log.debug("Call method saveEvent() for event with id = " + event.getId());

        long id = event.getId();
        String title = event.getTitle();
        Date date = event.getDate();
        int price = event.getPrice();
        String status = event.getStatus();
        String description = event.getDescription();
        long locationId = event.getLocationId();

        try {
            jdbcTemplate.update(
                    SQL_SAVE_EVENT,
                    id, title, date, price, status, description, locationId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVEEVENT_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_SAVEEVENT_METHOD, ex);
        }
    }

    public void updateEvent(Event event) {
        log.debug("Call method updateEvent() for event with id = " + event.getId());
        try {
            update(event);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_UPDATE_EVENT_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_UPDATE_EVENT_METHOD, ex);
        }
    }

    private void update(Event event) {
        long id = event.getId();
        String title = event.getTitle();
        Date date = event.getDate();
        int price = event.getPrice();
        String status = event.getStatus();
        String description = event.getDescription();
        long locationId = event.getLocationId();
        
        jdbcTemplate.update(
                SQL_UPDATE_EVENT,
                title, date, price, status, description, locationId, id
        );
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() for event with id = " + id);

        try {
            deleteEvent(id);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }

    private void deleteEvent(long id) {
        jdbcTemplate.update(
                SQL_DELETE_EVENT,
                id
        );
    }

    @Override
    public List<String> getAllCategoriesByEventId(long id) {
        log.debug("Call method getAllCategoriesByEventId() for event with id = " + id);

        List<Long> categoryIDs = getCategoryIDs(id);

        List<String> categoryNames = convertToNames(categoryIDs);

        if (log.isDebugEnabled()) {
            log.debug("Category names are " + categoryNames);
        }
        return categoryNames;
    }

    private List<String> convertToNames(List<Long> input) {
        log.debug("Call method convertToNames()");

        try {
            List<String> names = getCategoryNames(input);

            return names;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_CONVERTTONAMES_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_CONVERTTONAMES_METHOD, ex);
        }
    }

    private List<Long> getCategoryIDs(long id) {
        log.debug("Call method getCategoryIDs()");

        try {
            List<Long> dataIDs = jdbcTemplate.queryForList(
                    SQL_SELECT_ALL_CATEGORIES_BY_EVENT_ID,
                    Long.class,
                    id
            );
            return dataIDs;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETCATEGORYIDS_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETCATEGORYIDS_METHOD, ex);
        }
    }

    private List<String> getCategoryNames(List<Long> inputData) {
        log.debug("Call method getCategoryNames()");

        List<String> result = new ArrayList<>();
        for (long id : inputData) {
            String categoryName = jdbcTemplate.queryForObject(
                    SQL_SELECT_CATEGORY_NAME_BY_ID,
                    String.class,
                    id
            );
            result.add(categoryName);
        }
        return result;
    }

    @Override
    public void assignCategory(long eventId, long categoryId) {
        log.debug("Call method addNewCategory()");

        try {
            jdbcTemplate.update(
                    SQL_ADD_NEW_CATEGORY,
                    eventId, categoryId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD, ex);
        }
    }

    @Override
    public void removeCategory(long eventId, long categoryId) {
        log.debug("Call method removeCategory()");

        try {
            jdbcTemplate.update(
                    SQL_DELETE_CATEGORY,
                    eventId, categoryId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD, ex);
        }
    }
}

