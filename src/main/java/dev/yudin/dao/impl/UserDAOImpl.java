package dev.yudin.dao.impl;

import dev.yudin.dao.EventDAO;
import dev.yudin.dao.UserDAO;
import dev.yudin.entities.Event;
import dev.yudin.entities.User;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.mappers.UserRowMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_SELECT_ALL_USERS_ORDER_BY = "SELECT * FROM users ORDER BY";
    private static final String SQL_SELECT_ALL_USERS_ORDER_BY_NAME = "SELECT * FROM users ORDER BY name";
    private static final String SQL_SELECT_ALL_EVENTS_BY_USER_ID = "" +
            "SELECT event_id " +
            "FROM event_subscriptions " +
            "WHERE user_id = ?";
    private static final String SQL_SAVE_USER = "" +
            "INSERT INTO users " +
            "(user_id, name, surname, email, login, password, type) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "" +
            "UPDATE users " +
            "SET name = ?, surname = ?, email = ?, login = ?, password = ?, type = ? " +
            "WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_ADD_NEW_EVENT = "" +
            "INSERT INTO event_subscriptions " +
            "(user_id, event_id) " +
            "VALUES (?, ?)";
    private static final String SQL_DELETE_EVENT = "" +
            "DELETE " +
            "FROM event_subscriptions " +
            "WHERE user_id = ? AND event_id = ?";
    private static final String SQL_SELECT_EVENT_NAME_BY_ID = "SELECT name FROM events WHERE event_id = ?";

    private static final String DEFAULT_SORT_BY_COLUMN_NAME = "name";

    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such user with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVEUSER_METHOD = "Error during call the method saveUser()";
    private static final String ERROR_MESSAGE_FOR_UPDATEUSER_METHOD = "Error during call the method updateUser()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";
    private static final String ERROR_MESSAGE_FOR_CONVERTTONAMES_METHOD = "Error during call the method convertToNames()";
    private static final String ERROR_MESSAGE_FOR_GETEVENTIDS_METHOD = "Error during call the method getEventIDs()";
    private static final String ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD = "Error during call the method assignEvent()";
    private static final String ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD = "Error during call the method removeEvent()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private UserRowMapper userRowMapper;

    @Autowired
    private EventDAO eventDAO;

    @Override
    public User getById(long id) {
        log.debug("Call method getById() with id = " + id);

        try {
            User user = jdbcTemplate.queryForObject(
                    SQL_SELECT_USER_BY_ID,
                    userRowMapper,
                    id
            );
            if (log.isDebugEnabled()) {
                log.debug("User is " + user);
            }
            return user;
        } catch (EmptyResultDataAccessException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new DataNotFoundException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    @Override
    public List<User> findAll(Pageable page) {
        log.debug("Call method findAll()");

        String sqlQuery = buildSqlQuery(page);

        try {
            List<User> users = jdbcTemplate.query(
                    sqlQuery,
                    userRowMapper
            );
            if (log.isDebugEnabled()) {
                log.debug("Users are " + users);
            }
            return users;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    private String buildSqlQuery(Pageable pageable) {
        log.debug("Call method buildSqlQuery()");

        String query = SQL_SELECT_ALL_USERS_ORDER_BY_NAME;
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
                SQL_SELECT_ALL_USERS_ORDER_BY + " %1$s %2$s LIMIT %3$d OFFSET %4$d",
                sortProperty, sortDirectionName, pageSize, pageOffset);

        return result;
    }

    @Override
    public void save(User user) {
        log.debug("Call method save() for user with id = " + user.getId());

        if (doesExist(user.getId())) {
            updateUser(user);
        } else {
            saveUser(user);
        }
    }

    public void saveUser(User user) {
        log.debug("Call method saveUser()");
        try {
            updateInDB(user);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVEUSER_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_SAVEUSER_METHOD, ex);
        }
    }

    private void updateInDB(User user) {
        jdbcTemplate.update(
                SQL_SAVE_USER,
                user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getLogin(), user.getPassword(), user.getType()
        );
    }

    public void updateUser(User user) {
        log.debug("Call method updateUser() for user with id = " + user.getId());

        long id = user.getId();
        String name = user.getName();
        String surname = user.getSurname();
        String email = user.getEmail();
        String login = user.getLogin();
        String password = user.getPassword();
        String type = user.getType();

        try {
            jdbcTemplate.update(
                    SQL_UPDATE_USER,
                    name, surname, email, login, password, type, id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_UPDATEUSER_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_UPDATEUSER_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() for user with id = " + id);
        try {
            jdbcTemplate.update(
                    SQL_DELETE_USER,
                    id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }

    @Override
    public List<Event> getAllEventsByUserId(long id, Pageable pageable) {
        log.debug("Call method getAllEventsByUserId() for user with id = " + id);

        List<Long> eventIDs = getEventIDs(id, pageable);

        List<Event> events = getEvents(eventIDs);

        if (log.isDebugEnabled()) {
            log.debug("Events are " + events);
        }
        return events;
    }

    private List<Event> getEvents(List<Long> input) {
        log.debug("Call method getEventNames()");

        List<Event> result = new ArrayList<>();

        for (long id : input) {
            Event event = eventDAO.getById(id);

            result.add(event);
        }
        return result;
    }

    private List<Long> getEventIDs(long id, Pageable pageable) {
        log.debug("Call method getEventIDs()");

        String sqlQuery = assembleSqlQuery(pageable);

        try {
            List<Long> dataIDs = jdbcTemplate.queryForList(
                    sqlQuery,
                    Long.class,
                    id
            );
            return dataIDs;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETEVENTIDS_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETEVENTIDS_METHOD, ex);
        }
    }

    private String assembleSqlQuery(Pageable pageable) {
        log.debug("Call method assembleSqlQuery()");

        String query = SQL_SELECT_ALL_EVENTS_BY_USER_ID;
        if (pageable != null) {
            query = assembleSqlQueryWithPageable(pageable);
        }
        log.debug("SQL query is " + query);
        return query;
    }

    private String assembleSqlQueryWithPageable(Pageable pageable) {
        log.debug("Call method assembleSqlQueryWithPageable()");

        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();

        String result = String.format(
                SQL_SELECT_ALL_EVENTS_BY_USER_ID + " LIMIT %1$d OFFSET %2$d",
                pageSize, pageOffset);

        return result;
    }

    @Override
    public void assignEvent(long userId, long eventId) {
        log.debug("Call method assignEvent()");

        try {
            jdbcTemplate.update(
                    SQL_ADD_NEW_EVENT,
                    userId, eventId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD, ex);
        }
    }

    @Override
    public void removeEvent(long userId, long eventId) {
        log.debug("Call method removeEvent()");

        try {
            jdbcTemplate.update(
                    SQL_DELETE_EVENT,
                    userId, eventId
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD, ex);
        }
    }
}

