package dev.yudin.dao;

import dev.yudin.entities.Event;

import java.util.List;

public interface EventDAO extends GenericDAO<Event> {

    List<String> getAllCategoriesByEventId(long id);

    void assignCategory(long eventId, long categoryId);

    void removeCategory(long eventId, long categoryId);
}

