package mate.academy.bookstore.repository.order;

import static mate.academy.bookstore.util.TestOrderProvider.validOrder;

import java.util.List;
import mate.academy.bookstore.model.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Verify findByEmail() method works for OrderRepository")
    @Sql(scripts = {
            "classpath:database/order/create-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order/remove-order.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ValidUserId_returnListOrder() {
        Order expected = validOrder();

        List<Order> actual = orderRepository.findAllByUserId(17L, null);

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(List.of(expected), actual);
    }
}
