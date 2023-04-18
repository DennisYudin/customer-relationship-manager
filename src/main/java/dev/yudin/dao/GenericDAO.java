package dev.yudin.dao;

import dev.yudin.exceptions.DataNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenericDAO<T> {

    T getById(long id);

    List<T> findAll(Pageable pageable);

    void save(T t);

    void delete(long id);

    default boolean doesExist(long id) {
        boolean valueExist = true;
        try {
            getById(id);
        } catch (DataNotFoundException ex) {
            valueExist = false;
        }
        return valueExist;
    }
}

