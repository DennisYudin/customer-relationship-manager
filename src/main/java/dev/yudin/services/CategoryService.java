package dev.yudin.services;

import dev.yudin.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService extends GenericService<Category>{

    List<Category> getByName(String name);

}
