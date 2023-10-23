package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestBookProvider.threeBooksFromDb;
import static mate.academy.bookstore.util.TestBookProvider.toBookDtoWithoutCategoryIds;
import static mate.academy.bookstore.util.TestCategoryProvider.updateRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import mate.academy.bookstore.util.TestCategoryProvider;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

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

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify create() method works for CategoryController")
    @Sql(scripts = {
            "classpath:database/category/remove-controller-created-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_ReturnCategoryDto() throws Exception {
        Category category = TestCategoryProvider.validCategoryForCreate();
        RequestCategoryDto requestDto = TestCategoryProvider.toRequestDto(category);
        CategoryDto expected = TestCategoryProvider.toCategoryDto(category);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        assertNotNull(actual.getId());
        reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAllCategories() method works for CategoryController")
    void getAllCategories_ValidPageable_ReturnListCategoryDto() throws Exception {
        List<CategoryDto> expected = TestCategoryProvider.threeCategoriesFromDb().stream()
                .map(TestCategoryProvider::toCategoryDto)
                .toList();

        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        System.out.println(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getById() method works for CategoryController")
    void getCategoryById_ValidBookId_ReturnCategoryDto() throws Exception {
        Category category = TestCategoryProvider.threeCategoriesFromDb().get(0);
        CategoryDto expected = TestCategoryProvider.toCategoryDto(category);
        Long expectedBookId = category.getId();

        MvcResult result = mockMvc.perform(
                        get("/categories/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/add-single-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/remove-single-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Verify update() method works for CategoryController")
    void update_ValidRequestDto_ReturnBookDto() throws Exception {
        RequestCategoryDto requestDto = updateRequestDto();
        CategoryDto expected = TestCategoryProvider.updateResponseDto(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put("/categories/" + 7)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() method works for CategoryController")
    @Sql(scripts = {
            "classpath:database/category/add-single-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/remove-single-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidCategoryId_Successful() throws Exception {
        mockMvc.perform(
                        delete("/categories/7")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getBooksByCategoryId() method works for CategoryController")
    void getBooksByCategoryId_ValidCategoryId_ReturnListBookDtoWithoutCategoryIds()
            throws Exception {
        List<BookDtoWithoutCategoryIds> expected = List.of(
                toBookDtoWithoutCategoryIds(threeBooksFromDb().get(0))
        );
        MvcResult result = mockMvc.perform(
                        get(
                                "/categories/" + 1L + "/books"
                                )
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class
        );
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
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
}
