package mate.academy.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify create() method works for BookController")
    @Sql(scripts = {
            "classpath:database/book/remove-controller-created-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_ReturnBookDto() throws Exception {
        Book book = validBookForCreate();
        CreateBookRequestDto requestDto = validRequestDto(book);
        BookDto expected = validBookDto(book);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        Assertions.assertNotNull(actual.getId());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAllBooks() method works for BookController")
    void getAllBooks_ValidPageable_ReturnListBookDto() throws Exception {
        List<BookDto> expected = allBooksFromDb().stream()
                .map(this::validBookDto)
                .toList();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getById() method works for BookController")
    void getBookById_ValidBookId_ReturnBookDto() throws Exception {
        Book book = allBooksFromDb().get(0);
        BookDto expected = validBookDto(book);
        Long expectedBookId = book.getId();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/books/" + expectedBookId)
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify search() method works for BookController")
    void searchBooks_ValidRequestDto_ReturnBookDtoList() throws Exception {
        List<BookDto> expected = List.of(validBookDto(allBooksFromDb().get(0)));
        BookSearchParametersDto requestDto = validAuthorSearch(expected.get(0).getAuthor());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/books/search")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify update() method works for BookController")
    void update_ValidRequestDto_ReturnBookDto() throws Exception {
        Book updatedBook = allBooksFromDb().get(1).setAuthor("Socrat");
        CreateBookRequestDto requestDto = validRequestDto(updatedBook);
        Long expectedBookId = updatedBook.getId();
        BookDto expected = validBookDto(updatedBook);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + expectedBookId)
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(expected, actual);
        //return update
        jsonRequest = objectMapper.writeValueAsString(
                validRequestDto(updatedBook.setAuthor(allBooksFromDb().get(1).getAuthor()))
        );
        result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/books/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BookDto returnedUpdate = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(validBookDto(allBooksFromDb().get(1)), returnedUpdate);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() method works for BookController")
    @Sql(scripts = {
            "classpath:database/book/add-book-for-delete-method.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/book/remove-book-for-delete-method.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidBookId_Successful() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/books/7")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
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

    private Book validBookForCreate() {
        return new Book()
                .setTitle("valid_test_title")
                .setAuthor("valid_test_author")
                .setIsbn("9788845292613")
                .setPrice(BigDecimal.valueOf(20.20))
                .setDescription("test_description")
                .setCoverImage("test_cover_image")
                .setCategories(Set.of(new Category().setId(1L)));
    }

    private List<Book> allBooksFromDb() {
        return List.of(
                new Book()
                        .setId(1L)
                        .setTitle("The Hobbit")
                        .setAuthor("J.R.R. Tolkien")
                        .setIsbn("9780395647394")
                        .setPrice(BigDecimal.valueOf(20.22))
                        .setCategories(Set.of(new Category().setId(1L))),
                new Book()
                        .setId(2L)
                        .setTitle("Republic")
                        .setAuthor("Plato")
                        .setIsbn("9788830104716")
                        .setPrice(BigDecimal.valueOf(21.99))
                        .setCategories(Set.of(new Category().setId(2L))),
                new Book()
                        .setId(3L)
                        .setTitle("1984")
                        .setAuthor("George Orwell")
                        .setIsbn("9780007123810")
                        .setPrice(BigDecimal.valueOf(19.55))
                        .setCategories(Set.of(new Category().setId(3L)))
        );
    }

    private CreateBookRequestDto validRequestDto(Book book) {
        Set<Long> categoriesId
                = book.getCategories().stream().map(Category::getId).collect(Collectors.toSet());
        return new CreateBookRequestDto(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage(),
                categoriesId
        );
    }

    private BookDto validBookDto(Book book) {
        Set<Long> categoriesId
                = book.getCategories().stream().map(Category::getId).collect(Collectors.toSet());
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategories(categoriesId);
    }

    private BookSearchParametersDto validAuthorSearch(String... strings) {
        return new BookSearchParametersDto(
                null,
                strings,
                null
        );
    }
}
