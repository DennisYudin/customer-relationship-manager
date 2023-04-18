package dev.yudin.dao;

import dev.yudin.entities.Event;
import dev.yudin.entities.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDAO extends GenericDAO<User> {

    List<Event> getAllEventsByUserId(long id, Pageable pageable);

    void assignEvent(long userId, long eventId);

    void removeEvent(long userId, long eventId);
}
