package dev.yudin.services;



import java.util.List;

public interface CustomerService<T> {

    T getBy(long id);

    List<T> findAll();

    void save(T t);

    void delete(long id);
}

