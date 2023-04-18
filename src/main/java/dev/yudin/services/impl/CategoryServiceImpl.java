package dev.yudin.services.impl;

import dev.yudin.dao.CategoryDAO;
import dev.yudin.entities.Category;
import dev.yudin.exceptions.DAOException;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.exceptions.ServiceException;
import dev.yudin.services.CategoryService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String ERROR_MESSAGE_FOR_GETBYID_METHOD = "Error during call the method getById()";
    private static final String ERROR_MESSAGE_FOR_GETBYNAME_METHOD = "Error during call the method getByName()";
    private static final String ERROR_MESSAGE_FOR_VALIDATE_METHOD = "id can not be less or equals zero";
    private static final String EMPTY_RESULT_MESSAGE = "There is no such category with such input = ";
    private static final String ERROR_MESSAGE_FOR_FINDALL_METHOD = "Error during call the method findAll()";
    private static final String ERROR_MESSAGE_FOR_SAVE_METHOD = "Error during call the method save()";
    private static final String ERROR_MESSAGE_FOR_DELETE_METHOD = "Error during call the method delete()";

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public Category getById(long id) {
        log.debug("Call method getById() with id = " + id);

        validateId(id);

        try {
            Category category = categoryDAO.getById(id);
            if (log.isDebugEnabled()) {
                log.debug("Category is " + category);
            }
            return category;
        } catch (DataNotFoundException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + id, ex);
            throw new ServiceException(EMPTY_RESULT_MESSAGE + id, ex);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_GETBYID_METHOD, ex);
        }
    }

    private void validateId(long id) {
        log.debug("Call method validateId() with id = " + id);
        if (id <= 0) {
            log.error(ERROR_MESSAGE_FOR_VALIDATE_METHOD);
            throw new ServiceException(ERROR_MESSAGE_FOR_VALIDATE_METHOD);
        }
    }

    @Override
    public List<Category> getByName(String name) {
        log.debug("Call method getByName() with name = " + name);

        try {
            List<Category> category = categoryDAO.getByName(name);
            if (log.isDebugEnabled()) {
                log.debug("Category is " + category);
            }
            return category;
        } catch (DataNotFoundException ex) {
            log.warn(EMPTY_RESULT_MESSAGE + name, ex);
            throw new ServiceException(EMPTY_RESULT_MESSAGE + name, ex);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_GETBYNAME_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_GETBYNAME_METHOD, ex);
        }
    }

    @Override
    public List<Category> findAll(Pageable pageable) {
        log.debug("Call method findAll()");

        try {
            List<Category> categories = categoryDAO.findAll(pageable);

            if (log.isDebugEnabled()) {
                log.debug("Categories are " + categories);
            }
            return categories;
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_FINDALL_METHOD, ex);
        }
    }

    @Override
    public void save(Category category) {
        log.debug("Call method save() for category with id = " + category.getId());

        try {
            categoryDAO.save(category);
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_SAVE_METHOD, ex);
        }
    }

    @Override
    public void delete(long id) {
        log.debug("Call method delete() with id = " + id);

        validateId(id);

        try {
            categoryDAO.delete(id);
            log.debug("Category with id = " + id + " is deleted in DB");
        } catch (DAOException ex) {
            log.error(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
            throw new ServiceException(ERROR_MESSAGE_FOR_DELETE_METHOD, ex);
        }
    }
}
