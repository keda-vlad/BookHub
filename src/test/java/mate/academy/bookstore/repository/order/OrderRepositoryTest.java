package mate.academy.bookstore.repository.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.User;
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
        Order order = new Order()
                .setId(1L)
                .setUser(new User()
                        .setId(17L)
                        .setEmail("some_email@exam.com")
                        .setPassword("Password")
                        .setFirstName("FirstName")
                        .setLastName("SecondName"))
                .setStatus(Order.Status.PENDING)
                .setTotal(BigDecimal.valueOf(99.99))
                .setOrderDate(LocalDateTime
                        .of(2012, Month.SEPTEMBER, 8, 0, 0))
                .setShippingAddress("some-address")
                .setOrderItems(Set.of(
                        new OrderItem()
                                .setId(1L)
                                .setBook(new Book()
                                        .setId(13L)
                                        .setTitle("some_title")
                                        .setIsbn("some-isbn")
                                        .setPrice(BigDecimal.valueOf(99.99)))
                                .setPrice(BigDecimal.valueOf(99.99))
                                .setQuantity(1)
                        ));
        List<Order> actual = orderRepository.findAllByUserId(17L, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(List.of(order), actual);
    }
}
