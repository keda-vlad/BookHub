package mate.academy.bookstore.repository.orderitem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.OrderItem;
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
class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
        setup(dataSource);
    }

    @Test
    @DisplayName("Verify findAllByUserIdAndOrderId() method works for OrderItemRepository")
    void findAllByUserIdAndOrderId_ValidUserIdAndOrderId_ReturnOrderItemList() {
        OrderItem expected = new OrderItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("some_title")
                        .setIsbn("some_isbn")
                        .setPrice(BigDecimal.valueOf(99.99)))
                .setPrice(BigDecimal.valueOf(99.99))
                .setQuantity(1);

        List<OrderItem> actual = orderItemRepository
                .findAllByUserIdAndOrderId(17L, 1L, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify findByUserIdAndItemIdAndOrderId() method works for OrderItemRepository")
    void findByUserIdAndItemIdAndOrderId_ValidIds_ReturnOrderItem() {
        OrderItem expected = new OrderItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("some_title")
                        .setIsbn("some_isbn")
                        .setPrice(BigDecimal.valueOf(99.99)))
                .setPrice(BigDecimal.valueOf(99.99))
                .setQuantity(1);

        Optional<OrderItem> actual = orderItemRepository
                .findByUserIdAndItemIdAndOrderId(17L, 1L, 1L);
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
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
                    new ClassPathResource("database/order/create-order.sql")
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/order/remove-order.sql")
            );
        }
    }
}
