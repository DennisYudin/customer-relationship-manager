package dev.yudin.dao;

import dev.yudin.entities.Category;

import java.util.List;

public interface CategoryDAO extends GenericDAO<Category> {

    List<Category> getByName(String name);

}

