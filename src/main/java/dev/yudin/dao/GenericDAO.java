package dev.yudin.dao;

import dev.yudin.exceptions.DataNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenericDAO<T> {
    T getById(int id);

    List<T> findAll();

    void save(T t);

    void delete(int id);
}

