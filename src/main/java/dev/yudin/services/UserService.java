package dev.yudin.services;

import dev.yudin.entities.Event;
import dev.yudin.entities.User;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService extends GenericService<User> {

    List<Event> getAllEventsByUserId(long id, Pageable pageable);

    void assignEvent(long firstId, long secondId);

    void removeEvent(long firstId, long secondId);
}
