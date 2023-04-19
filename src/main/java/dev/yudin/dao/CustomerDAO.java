package dev.yudin.dao;

import java.util.List;

public interface CustomerDAO<T> {

    T getBy(long id);

    List<T> findAll();

    void save(T t);

    void delete(long id);
}

