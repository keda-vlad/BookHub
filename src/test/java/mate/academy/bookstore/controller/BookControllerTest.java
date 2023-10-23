package mate.academy.bookstore.controller;

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
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.util.TestBookProvider;
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
class BookControllerTest {
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
    @DisplayName("Verify create() method works for BookController")
    @Sql(scripts = {
            "classpath:database/book/remove-controller-created-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_ReturnBookDto() throws Exception {
        Book book = TestBookProvider.validBook();
        CreateBookRequestDto requestDto = TestBookProvider.toCreateBookRequestDto(book);
        BookDto expected = TestBookProvider.toBookDto(book);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAllBooks() method works for BookController")
    void getAllBooks_ValidPageable_ReturnListBookDto() throws Exception {
        List<BookDto> expected = TestBookProvider.threeBooksFromDb().stream()
                .map(TestBookProvider::toBookDto)
                .toList();

        MvcResult result = mockMvc.perform(
                get("/books")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getById() method works for BookController")
    void getBookById_ValidBookId_ReturnBookDto() throws Exception {
        Book book = TestBookProvider.threeBooksFromDb().get(0);
        BookDto expected = TestBookProvider.toBookDto(book);
        Long expectedBookId = book.getId();

        MvcResult result = mockMvc.perform(
                get("/books/" + expectedBookId)
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertTrue(reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify search() method works for BookController")
    void searchBooks_ValidRequestDto_ReturnBookDtoList() throws Exception {
        Book book = TestBookProvider.threeBooksFromDb().get(0);
        List<BookDto> expected = List.of(TestBookProvider.toBookDto(book));
        BookSearchParametersDto requestDto = TestBookProvider.toAuthorSearch(book);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                get("/books/search")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/book/add-single-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/remove-single-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Verify update() method works for BookController")
    void update_ValidRequestDto_ReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestBookProvider.updateRequestDto();
        BookDto expected = TestBookProvider.updateResponseDto(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                put("/books/" + expected.getId())
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() method works for BookController")
    @Sql(scripts = {
            "classpath:database/book/add-single-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/remove-single-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidBookId_Successful() throws Exception {
        mockMvc.perform(
                        delete("/books/7")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(status().isNoContent());
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
