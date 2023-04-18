package dev.yudin.services;

import dev.yudin.dto.EventDTO;
import dev.yudin.entities.Event;

import java.util.List;

public interface EventService extends GenericService<Event> {

    List<String> getAllCategoriesByEventId(long id);

    void addNewCategory(long eventId, long categoryId);

    void removeCategory(long eventId, long categoryId);

    EventDTO getEventWithDetails(long id);
}
