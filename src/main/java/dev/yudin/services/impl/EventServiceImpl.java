package dev.yudin.services.impl;

import dev.yudin.dao.CategoryDAO;
import dev.yudin.dao.EventDAO;
import dev.yudin.dao.LocationDAO;
import dev.yudin.dto.EventDTO;
import dev.yudin.dto.mapper.EventMapper;
import dev.yudin.entities.Event;
import dev.yudin.entities.Location;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.exceptions.ServiceException;
import dev.yudin.services.EventService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class EventServiceImpl implements EventService {
    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String ERROR_MESSAGE_FOR_VALIDATE_METHOD = "id can not be less or equals zero";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such event with id = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVE_METHOD = "Error during call the method save()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";
    private static final String ERROR_MESSAGE_FOR_GETALLCATEGOIESBYEVENTID_METHOD = "Error during call the method getAllCategoriesByEventId()";
    private static final String ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD = "Error during call the method addNewCategory()";
    private static final String ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD = "Error during call the method removeCategory()";
    private static final String EMPTY_RESULT_MESSAGE_FOR_GETLOCATIONBYID_METHOD = "There is no such location with id = ";
    private static final String ERROR_MESSAGE_FOR_FOR_GETLOCATIONBYID_METHOD = "Error during call the method getLocationById()";

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private LocationDAO locationDAO;

    @Autowired
    private EventMapper eventMapper;

    @Override
    public Event getById(long id) {
        log.debug("Call method getById() with id = " + id);

        validateId(id);

        try {
            Event event = eventDAO.getById(id);
            if (log.isDebugEnabled()) {
                log.debug("Event is " + event);
            }
            return event;
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
    public List<Event> findAll(Pageable pageable) {
        log.debug("Call method findAll()");

        try {
            List<Event> events = eventDAO.findAll(pageable);
            if (log.isDebugEnabled()) {
                log.debug("Events are " + events);
            }
            return events;
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    @Override
    public void save(Event event) {
        log.debug("Call method save() for event with id = " + event.getId());

        validateId(event.getId());

        try {
            eventDAO.save(event);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() with event id = " + id);

        validateId(id);

        try {
            eventDAO.delete(id);
            log.debug("Event with id = " + id + " is deleted in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }

    @Override
    public List<String> getAllCategoriesByEventId(long id) {
        log.debug("Call method getAllCategoriesByEventId() with id = " + id);

        validateId(id);

        try {
            List<String> categories = eventDAO.getAllCategoriesByEventId(id);
            if (log.isDebugEnabled()) {
                log.debug("Categories are " + categories);
            }
            return categories;
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_GETALLCATEGOIESBYEVENTID_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_GETALLCATEGOIESBYEVENTID_METHOD, ex);
        }
    }

    @Override
    public void addNewCategory(long eventId, long categoryId) {
        log.debug("Call method addNewCategory() for " +
                "event id = " + eventId + " and category id = " + categoryId);
        try {
            eventDAO.assignCategory(eventId, categoryId);
            log.debug("Category with id = " + categoryId + " is added in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_ADDNEWCATEGORY_METHOD, ex);
        }
    }

    @Override
    public void removeCategory(long eventId, long categoryId) {
        log.debug("Call method removeCategory() for " +
                "event id = " + eventId + " and category id = " + categoryId);
        try {
            eventDAO.removeCategory(eventId, categoryId);
            log.debug("Category with id = " + categoryId + " is deleted in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_REMOVECATEGORY_METHOD, ex);
        }
    }

    @Override
    public EventDTO getEventWithDetails(long id) {
        log.debug("Call method getEventWithDetails() with id = " + id);

        Event event = getById(id);

        List<String> categoryNames = getAllCategoriesByEventId(id);

        Location location = getLocationById(event.getLocationId());

        EventDTO eventDTO = eventMapper.convertToDTO(event, categoryNames, location);

        if (log.isDebugEnabled()) {
            log.debug("EventDTO is " + eventDTO);
        }
        return eventDTO;
    }

    private Location getLocationById(long id) {
        log.debug("Call method getLocationById() with id = " + id);

        try {
            Location location = locationDAO.getById(id);
            if (log.isDebugEnabled()) {
                log.debug("Location is " + location);
            }
            return location;
        } catch (DataNotFoundException ex) {
            log.warn(EMPTY_RESULT_MESSAGE_FOR_GETLOCATIONBYID_METHOD + id, ex);
            throw new ServiceException(EMPTY_RESULT_MESSAGE_FOR_GETLOCATIONBYID_METHOD + id, ex);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_FOR_GETLOCATIONBYID_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_FOR_GETLOCATIONBYID_METHOD, ex);
        }
    }
}

