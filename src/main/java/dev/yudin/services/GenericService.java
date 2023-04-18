package dev.yudin.services;



import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenericService<T> {

    T getById(long id);

    List<T> findAll(Pageable pageable);

    void save(T t);

    void delete(long id);
}

