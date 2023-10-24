package mate.academy.bookstore.repository.orderitem;

import static mate.academy.bookstore.util.TestOrderProvider.validOrderItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.model.OrderItem;
import org.junit.jupiter.api.AfterAll;
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

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @Test
    @DisplayName("Verify findAllByUserIdAndOrderId() method works for OrderItemRepository")
    void findAllByUserIdAndOrderId_ValidUserIdAndOrderId_ReturnOrderItemList() {
        OrderItem expected = validOrderItem();

        List<OrderItem> actual = orderItemRepository
                .findAllByUserIdAndOrderId(17L, 1L, null);

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Verify findByUserIdAndItemIdAndOrderId() method works for OrderItemRepository")
    void findByUserIdAndItemIdAndOrderId_ValidIds_ReturnOrderItem() {
        OrderItem expected = validOrderItem();

        Optional<OrderItem> actual = orderItemRepository
                .findByUserIdAndItemIdAndOrderId(17L, 1L, 1L);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
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
