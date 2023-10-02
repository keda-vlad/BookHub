package mate.academy.bookstore.repository.shoppingcart;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
        setup(dataSource);
    }

    @Test
    @DisplayName("Verify findByUserId() method works for ShoppingCartRepository")
    void findByUserId_ValidUserId_returnOptionalShoppingCart() {
        ShoppingCart expected = shoppingCartFromDb();
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(17L);
        System.out.println(expected.getCartItems());
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    private ShoppingCart shoppingCartFromDb() {
        return new ShoppingCart()
                .setId(17L)
                .setUser(new User()
                        .setId(17L)
                        .setEmail("some_email@exam.com")
                        .setPassword("Password")
                        .setFirstName("FirstName")
                        .setLastName("SecondName"))
                .setCartItems(Set.of(
                        new CartItem()
                                .setId(1L)
                                .setQuantity(10)
                                .setBook(new Book()
                                        .setId(1L)
                                        .setTitle("some_title")
                                        .setIsbn("some_isbn")
                                        .setPrice(BigDecimal.valueOf(99.99)))));
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
                    new ClassPathResource("database/shoppingcart/create-shopping-cart.sql")
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shoppingcart/remove-shopping-cart.sql")
            );
        }
    }
}
