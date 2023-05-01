package dev.yudin.services;


import java.util.List;

public interface GenericService<T> {

    T getById(int id);

    List<T> findAll();

    void save(T t);

    void delete(int id);
}

