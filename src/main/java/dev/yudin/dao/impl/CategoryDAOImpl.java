package dev.yudin.dao.impl;

import dev.yudin.dao.CategoryDAO;
import dev.yudin.entities.Category;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.exceptions.ValueExistException;
import dev.yudin.mappers.CategoryRowMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j
@Repository("categoryDAO")
public class CategoryDAOImpl implements CategoryDAO {
    private static final String SQL_SELECT_CATEGORY_BY_ID = "SELECT * FROM categories WHERE category_id = ?";
    private static final String SQL_SELECT_CATEGORY_BY_NAME = "SELECT * FROM categories WHERE name LIKE ?";
    private static final String SQL_SELECT_ALL_CATEGORIES_ORDER_BY = "SELECT * FROM categories ORDER BY";
    private static final String SQL_SELECT_ALL_CATEGORIES_ORDER_BY_NAME = "SELECT * FROM categories ORDER BY name";
    private static final String SQL_SAVE_CATEGORY = "INSERT INTO categories (name) VALUES (?)";
    private static final String SQL_UPDATE_CATEGORY = "UPDATE categories SET name = ? WHERE category_id = ?";
    private static final String SQL_DELETE_CATEGORY = "DELETE FROM categories WHERE category_id = ?";

    private static final String DEFAULT_SORT_BY_COLUMN_NAME = "name";

    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String ERROR_MESSAGE_FOR_GETBYNAME_METHOD = "Error during call the method getByName()";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such category with such input = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVECATEGORY_METHOD = "Error during call the method saveCategory()";
    private static final String ERROR_MESSAGE_FOR_UPDATECATEGORY_METHOD = "Error during call the method updateCategory()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private CategoryRowMapper categoryRowMapper;

    @Override
    public Category getById(long id) {
        log.debug("Call method getById() with id = " + id);
        try {
            return getCategoryBy(id);
        } catch (EmptyResultDataAccessException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new DataNotFoundException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    private Category getCategoryBy(long id) {
        Category category = jdbcTemplate.queryForObject(
                SQL_SELECT_CATEGORY_BY_ID,
                categoryRowMapper,
                id
        );
        if (log.isDebugEnabled()) {
            log.debug("Category is " + category);
        }
        return category;
    }

    @Override
    public List<Category> getByName(String name) {
        log.debug("Call method getByName() with name = " + name);

        try {
            List<Category> categories = jdbcTemplate.query(
                    SQL_SELECT_CATEGORY_BY_NAME,
                    categoryRowMapper,
                    "%" + name + "%"
            );
            if (log.isDebugEnabled()) {
                log.debug("Category is " + categories);
            }
            return categories;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYNAME_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_GETBYNAME_METHOD, ex);
        }
    }

    @Override
    public List<Category> findAll(Pageable page) {
        log.debug("Call method findAll()");

        String sqlQuery = buildSqlQuery(page);

        try {
            List<Category> categories = jdbcTemplate.query(
                    sqlQuery,
                    categoryRowMapper
            );
            if (log.isDebugEnabled()) {
                log.debug("Categories are " + categories);
            }
            return categories;
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    private String buildSqlQuery(Pageable pageable) {
        log.debug("Call method buildSqlQuery()");

        String query = SQL_SELECT_ALL_CATEGORIES_ORDER_BY_NAME;
        if (pageable != null) {
            query = buildSqlQueryWith(pageable);
        }
        log.debug("SQL query is " + query);
        return query;
    }

    private String buildSqlQueryWith(Pageable pageable) {
        log.debug("Call method buildSqlQueryWithPageable()");

        Sort.Order order;
        if (pageable.getSort().isEmpty()) {
            order = Sort.Order.by(DEFAULT_SORT_BY_COLUMN_NAME);
        } else {
            order = pageable.getSort().iterator().next();
        }
        String query = collectSqlQuery(pageable, order);

        return query;
    }

    private String collectSqlQuery(Pageable pageable, Sort.Order sort) {
        log.debug("Call method collectSqlQuery()");

        String sortProperty = sort.getProperty();
        String sortDirectionName = sort.getDirection().name();
        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();

        @SuppressWarnings("uncheck")
        String result = String.format(
                SQL_SELECT_ALL_CATEGORIES_ORDER_BY + " %1$s %2$s LIMIT %3$d OFFSET %4$d",
                sortProperty, sortDirectionName, pageSize, pageOffset);

        return result;
    }

    @Override
    public void save(Category category) {
        log.debug("Call method save() for category with id = " + category.getId());

        if (doesExist(category.getId())) {
            updateCategory(category);
        } else {
            if (validateName(category)) {
                throw new ValueExistException(category.getTitle() + " already exist");
            } else {
                saveCategory(category);
            }
        }
    }

    public boolean validateName(Category category) {
        boolean valueExist = false;

        String newCategory = category.getTitle();

        if (!getByName(newCategory).isEmpty()) {
            valueExist = true;
        }
        return valueExist;
    }

    public void saveCategory(Category category) {
        log.debug("Call method saveCategory() for category with id = " + category.getId());

        String title = category.getTitle();
        try {
            jdbcTemplate.update(
                    SQL_SAVE_CATEGORY,
                    title
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVECATEGORY_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_SAVECATEGORY_METHOD, ex);
        }
    }

    public void updateCategory(Category category) {
        log.debug("Call method updateCategory() for category with id = " + category.getId());

        long id = category.getId();
        String title = category.getTitle();
        try {
            jdbcTemplate.update(
                    SQL_UPDATE_CATEGORY,
                    title, id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_UPDATECATEGORY_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_UPDATECATEGORY_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() for category with id = " + id);

        try {
            jdbcTemplate.update(
                    SQL_DELETE_CATEGORY,
                    id
            );
        } catch (DataAccessException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new DAOException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }
}

