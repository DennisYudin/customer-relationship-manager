package dev.yudin.services.impl;

import dev.yudin.dao.EventDAO;
import dev.yudin.dao.UserDAO;
import dev.yudin.entities.Event;
import dev.yudin.entities.User;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.exceptions.ServiceException;
import dev.yudin.services.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class UserServiceImpl implements UserService {
    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String ERROR_MESSAGE_FOR_VALIDATE_METHOD = "id can not be less or equals zero";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such user with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVE_METHOD = "Error during call the method save()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";
    private static final String ERROR_MESSAGE_FOR_GETALLEVENTS_METHOD = "Error during call the method getAllEventsByUserId()";
    private static final String ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD = "Error during call the method assignEvent()";
    private static final String ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD = "Error during call the method removeEvent()";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EventDAO eventDAO;

    @Override
    public User getById(long id) {
        log.debug("Call method getById() with id = " + id);

        validateId(id);

        try {
            User user = userDAO.getById(id);
            if (log.isDebugEnabled()) {
                log.debug("User is " + user);
            }
            return user;
        } catch (DataNotFoundException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new ServiceException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    private void validateId(long id) {
        log.debug("Call method validateId() with id = " + id);
        if (id <= 0) {
            log.error(ERROR_MESSAGE_FOR_VALIDATE_METHOD);
            throw new ServiceException(ERROR_MESSAGE_FOR_VALIDATE_METHOD);
        }
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        log.debug("Call method findAll()");

        try {
            List<User> users = userDAO.findAll(pageable);
            if (log.isDebugEnabled()) {
                log.debug("Users are " + users);
            }
            return users;
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    @Override
    public void save(User user) {
        log.debug("Call method save() for user with id = " + user.getId());

        validateId(user.getId());

        try {
            userDAO.save(user);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() with id = " + id);

        validateId(id);

        try {
            userDAO.delete(id);
            log.debug("User with id = " + id + " is deleted in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }

    @Override
    public List<Event> getAllEventsByUserId(long id, Pageable pageable) {
        log.debug("Call method getAllEventsByUserId() with id = " + id);

        validateId(id);

        try {
            List<Event> events = userDAO.getAllEventsByUserId(id, pageable);
            if (log.isDebugEnabled()) {
                log.debug("Events are " + events);
            }
            return events;
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_GETALLEVENTS_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_GETALLEVENTS_METHOD, ex);
        }
    }

    @Override
    public void assignEvent(long userId, long eventId) {
        log.debug("Call method assignEvent() for " +
                "user id = " + userId + " and event id = " + eventId);
        try {
            userDAO.assignEvent(userId, eventId);
            log.debug("Event with id = " + eventId + " is added in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_ASSIGNEVENT_METHOD, ex);
        }
    }

    @Override
    public void removeEvent(long userId, long eventId) {
        log.debug("Call method removeEvent() for " +
                "user id = " + userId + " and event id = " + eventId);
        try {
            userDAO.removeEvent(userId, eventId);
            log.debug("Event with id = " + eventId + " is deleted in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_REMOVEEVENT_METHOD, ex);
        }
    }
}

