package mate.academy.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import mate.academy.bookstore.model.Category;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        teardown(dataSource);
        setup(dataSource);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify create() method works for CategoryController")
    @Sql(scripts = {
            "classpath:database/category/remove-controller-created-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_ReturnCategoryDto() throws Exception {
        Category category = validCategoryForCreate();
        RequestCategoryDto requestDto = validRequestDto(category);
        CategoryDto expected = validCategoryDto(category);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAllBooks() method works for CategoryController")
    void getAllCategories_ValidPageable_ReturnListCategoryDto() throws Exception {
        List<CategoryDto> expected = allCategoriesFromDb().stream()
                .map(this::validCategoryDto)
                .toList();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/categories")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getById() method works for CategoryController")
    void getCategoryById_ValidBookId_ReturnCategoryDto() throws Exception {
        Category category = allCategoriesFromDb().get(0);
        CategoryDto expected = validCategoryDto(category);
        Long expectedBookId = category.getId();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/categories/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify update() method works for CategoryController")
    void update_ValidRequestDto_ReturnBookDto() throws Exception {
        Category updatedCategory =
                allCategoriesFromDb().get(1).setDescription("Some new description");
        RequestCategoryDto requestDto = validRequestDto(updatedCategory);
        Long expectedBookId = updatedCategory.getId();
        CategoryDto expected = validCategoryDto(updatedCategory);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/categories/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertEquals(expected, actual);
        //return update
        jsonRequest = objectMapper.writeValueAsString(
                validRequestDto(updatedCategory.setDescription(
                        allCategoriesFromDb().get(1).getDescription())
                )
        );
        result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/categories/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CategoryDto returnedUpdate = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertEquals(
                validCategoryDto(allCategoriesFromDb().get(1)),
                returnedUpdate);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() method works for CategoryController")
    @Sql(scripts = {
            "classpath:database/book/add-book-for-delete-method.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/remove-book-for-delete-method.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidBookId_Successful() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/categories/7")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAllBooks() method works for CategoryController")
    void getBooksByCategoryId_ValidCategoryId_ReturnListBookDtoWithoutCategoryIds()
            throws Exception {
        Long expectedBookId = allCategoriesFromDb().get(0).getId();
        List<BookDtoWithoutCategoryIds> expected = List.of(
                new BookDtoWithoutCategoryIds(
                        1L,
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        "9780395647394",
                        BigDecimal.valueOf(20.22),
                        null,
                        null
                )
        );
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                "/categories/" + expectedBookId + "/books"
                                )
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class
        );
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void setup(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/add-three-books-and-categories.sql")
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/remove-three-books-and-categories.sql")
            );
        }
    }

    private Category validCategoryForCreate() {
        return new Category()
                .setName("test_name")
                .setDescription("test_description");
    }

    private List<Category> allCategoriesFromDb() {
        return List.of(
                new Category()
                        .setId(1L)
                        .setName("Fantasy Adventure")
                        .setDescription("Fantasy Adventure"),
                new Category()
                        .setId(2L)
                        .setName("Philosophy")
                        .setDescription("Philosophy"),
                new Category()
                        .setId(3L)
                        .setName("Social science fiction")
                        .setDescription("Social science fiction")
        );
    }

    private RequestCategoryDto validRequestDto(Category category) {
        return new RequestCategoryDto(
                category.getName(),
                category.getDescription()
        );
    }

    private CategoryDto validCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
