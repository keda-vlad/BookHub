package mate.academy.bookstore.repository.book;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.model.Book;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
        setup(dataSource);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works for BookRepository")
    void findAllByCategoryId_ValidCategoryIdAndPageable_returnBookList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllByCategoryId(1L, pageable);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("The Hobbit", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("Verify findAllWithCategories() method works for BookRepository")
    void findAllWithCategories_ValidPageable_returnBookList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllWithCategories(pageable);
        Assertions.assertEquals(3, actual.size());
        Assertions.assertEquals("The Hobbit", actual.get(0).getTitle());
        Assertions.assertEquals("Republic", actual.get(1).getTitle());
        Assertions.assertEquals("1984", actual.get(2).getTitle());
    }

    @Test
    @DisplayName("Verify findByIdWithCategories() method works for BookRepository")
    void findByIdWithCategories_ValidCategoryIdAndPageable_returnBookList() {
        Optional<Book> actual = bookRepository.findByIdWithCategories(1L);
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals("The Hobbit", actual.get().getTitle());
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
}
