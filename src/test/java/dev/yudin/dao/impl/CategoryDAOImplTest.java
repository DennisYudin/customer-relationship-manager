package dev.yudin.dao.impl;

import dev.yudin.configs.AppConfigTest;
import dev.yudin.dao.CategoryDAO;
import dev.yudin.entities.Category;
import dev.yudin.exceptions.DataNotFoundException;
import dev.yudin.services.CategoryService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfigTest.class)
@Sql(scripts = {
//        "file:src/test/resources/createDataBase.sql",
        "file:src/test/resources/createTables.sql",
        "file:src/test/resources/populateTables.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/cleanUpTables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WebAppConfiguration
class CategoryDAOImplTest {
    private static final String SQL_SELECT_CATEGORY_ID = "SELECT category_id FROM categories WHERE name = ?";
    private static final String SQL_SELECT_ALL_CATEGORIES_ID = "SELECT category_id FROM categories";

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void pageTest() {
        int pageNo = 1;
        int pageSize = 2;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
//        Pageable pageable = null;

        List<Category> categories = categoryService.findAll(null);

        int allElements = categories.size();

        int amountPages = (int) Math.ceil((double) allElements / pageSize);

        Page<Category> categoriesPage = new PageImpl<>(categories);

        System.out.println();
        System.out.println("page content: " + categoriesPage.getContent());
        System.out.println("currentPage: " + pageNo);
        System.out.println("totalPages: " + categoriesPage.getTotalPages());
        System.out.println("totalItems: " + categoriesPage.getTotalElements());
        System.out.println("amountPages:" + amountPages);

        if (amountPages > 0) {
            List<Integer> pageNumbers = IntStream
                    .rangeClosed(1, amountPages)
                    .boxed()
                    .collect(toList());
            System.out.println("pageNumbers: " + pageNumbers);
        }
        System.out.println();
    }

    @BeforeAll
    public static void initDB(){
        initDatabase();
    }

    public static void initDatabase() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "1234");
            statement = connection.createStatement();
            statement.executeQuery("SELECT count(*) FROM pg_database WHERE datname = 'mydb'");
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count <= 0) {
                statement.executeUpdate("CREATE DATABASE database_name");
                System.out.println("Database created.");
            } else {
                System.out.println("Database already exist.");
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Test
    void getById_ShouldReturnCategory_WhenInputIsExistIdValue() {

//        initDatabase();

        Category expectedCategory = getCategory(1, "exhibition");
        Category actualCategory = categoryDAO.getById(1);

        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void getById_ShouldThrowDataNotFoundException_WhenInputIsIncorrectId() {

        assertThrows(DataNotFoundException.class, () -> categoryDAO.getById(-1));
    }

    @Test
    void getByName_ShouldReturnCategory_WhenInputIsThreeFirstLettersOfCategory() {

        Category category = getCategory(1, "exhibition");

        List<Category> actualCategories = categoryDAO.getByName("exh");
        List<Category> expectedCategories = new ArrayList<>();

        expectedCategories.add(category);

        assertEquals(expectedCategories.size(), actualCategories.size());
        assertTrue(expectedCategories.containsAll(actualCategories));
    }

    @Test
    void getByName_ShouldReturnCategory_WhenInputIsCategoryName() {

        Category category = getCategory(1, "exhibition");

        List<Category> actualCategories = categoryDAO.getByName("exhibition");
        List<Category> expectedCategories = new ArrayList<>();

        expectedCategories.add(category);

        assertEquals(expectedCategories.size(), actualCategories.size());
        assertTrue(expectedCategories.containsAll(actualCategories));
    }

    @Test
    void getByName_ShouldEmptyList_WhenInputIsDoesNotExistCategoryName() {

        List<Category> actualCategories = categoryDAO.getByName("DoesNotExistValue");

        assertTrue(actualCategories.isEmpty());
    }

    @Test
    void getAutoGeneratedKeyTest() {

        String name = "movie";

        KeyHolder keyHolder = getAutoGeneratedKey(name);

        assertEquals(5, keyHolder.getKey());

    }

    public KeyHolder getAutoGeneratedKey(String name) {
        String sqlQuery = "INSERT INTO categories (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"category_id"});

                        ps.setString(1, name);

                        return ps;
                    }
                }, keyHolder);

        return keyHolder;
    }

    @Test
    void findAll_ShouldReturnAllCategoriesSortedByName_WhenInputIsPageRequestWithoutSort() {

        Pageable sortedByName = PageRequest.of(0, 4);

        List<Category> actualCategories = categoryDAO.findAll(sortedByName);
        List<String> expectedCategoryNames = new ArrayList<>();

        expectedCategoryNames.add("Art concert");
        expectedCategoryNames.add("exhibition");
        expectedCategoryNames.add("movie");
        expectedCategoryNames.add("theatre");

        for (int i = 0; i < actualCategories.size(); i++) {

            String actualCategoryName = actualCategories.get(i).getTitle();
            String expectedCategoryName = expectedCategoryNames.get(i);

            assertEquals(expectedCategoryName, actualCategoryName);
        }
    }

    @Test
    void findAll_ShouldReturnTwoCategoriesSortedByName_WhenInputIsPageRequestWithSizeTwoWithoutSort() {

        Pageable sortedByName = PageRequest.of(0, 2);

        List<Category> actualCategoryNames = categoryDAO.findAll(sortedByName);
        List<String> expectedCategoryNames = new ArrayList<>();

        expectedCategoryNames.add("Art concert");
        expectedCategoryNames.add("exhibition");

        for (int i = 0; i < actualCategoryNames.size(); i++) {

            String actualCategoryName = actualCategoryNames.get(i).getTitle();
            String expectedCategoryName = expectedCategoryNames.get(i);

            assertEquals(expectedCategoryName, actualCategoryName);
        }
    }

    @Test
    void findAll_ShouldReturnAllCategoriesSortedById_WhenInputIsPageRequestWithSortValue() {

        Pageable sortedById = PageRequest.of(0, 1, Sort.by("category_id"));

        List<Category> actualCategoryIDs = categoryDAO.findAll(sortedById);
        List<Integer> expectedCategoryIDs = new ArrayList<>();

        expectedCategoryIDs.add(1);
        expectedCategoryIDs.add(2);
        expectedCategoryIDs.add(3);
        expectedCategoryIDs.add(4);

        for (int i = 0; i < actualCategoryIDs.size(); i++) {
            long actualCategoryID = actualCategoryIDs.get(i).getId();
            long expectedCategoryID = expectedCategoryIDs.get(i);

            assertEquals(expectedCategoryID, actualCategoryID);
        }
    }

    @Test
    void findAll_ShouldReturnAllCategoriesSortedByName_WhenPageIsNull() {

        Pageable page = null;

        List<Category> actualCategoryNames = categoryDAO.findAll(page);
        List<String> expectedCategoryNames = new ArrayList<>();

        expectedCategoryNames.add("Art concert");
        expectedCategoryNames.add("exhibition");
        expectedCategoryNames.add("movie");
        expectedCategoryNames.add("theatre");

        for (int i = 0; i < actualCategoryNames.size(); i++) {

            String actualCategoryName = actualCategoryNames.get(i).getTitle();
            String expectedCategoryName = expectedCategoryNames.get(i);

            assertEquals(expectedCategoryName, actualCategoryName);
        }
    }

    @Test
    void save_ShouldSaveCategory_WhenInputIsCategoryObjectWithIdAndName() {

        Category newCategory = getCategory(0, "opera");

        categoryDAO.save(newCategory);

        String checkName = "opera";

        long expectedId = 5;
        Long actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_CATEGORY_ID,
                Long.class,
                checkName
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    void save_ShouldUpdateExistedCategory_WhenInputIsCategoryObjectWithIdAndName() {

        Category updatedCategory = getCategory(1, "opera");

        categoryDAO.save(updatedCategory);

        String checkName = "opera";

        int expectedId = 1;
        Integer actualId = jdbcTemplate.queryForObject(
                SQL_SELECT_CATEGORY_ID,
                Integer.class,
                checkName
        );
        assertEquals(expectedId, actualId);
    }

    @Test
    void delete_ShouldDeleteCategoryById_WhenInputIsId() {

        long categoryId = 1;

        categoryDAO.delete(categoryId);

        List<Long> actualId = jdbcTemplate.queryForList(
                SQL_SELECT_ALL_CATEGORIES_ID,
                Long.class
        );
        int expectedSize = 3;
        int actualSize = actualId.size();

        long checkedId = 1;

        assertEquals(expectedSize, actualSize);
        assertFalse(actualId.contains(checkedId));
    }

    private Category getCategory(long id, String title) {
        Category category = new Category();
        category.setId(id);
        category.setTitle(title);

        return category;
    }
}

