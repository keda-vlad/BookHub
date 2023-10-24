package mate.academy.bookstore.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.User;

public class TestOrderProvider {
    public static OrderDto validOrderDto() {
        return new OrderDto(
                1L,
                17L,
                Set.of(new OrderItemDto(1L, 13L, 1)),
                LocalDateTime.parse("2012-09-08T00:00:00"),
                BigDecimal.valueOf(99.99),
                Order.Status.PENDING
        );
    }

    public static OrderDto validDeliveredOrderDto() {
        return new OrderDto(
                1L,
                17L,
                Set.of(new OrderItemDto(1L, 13L, 1)),
                LocalDateTime.parse("2012-09-08T00:00:00"),
                BigDecimal.valueOf(99.99),
                Order.Status.DELIVERED
        );
    }

    public static Order validOrder() {
        return new Order()
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
    }

    public static OrderItem validOrderItem() {
        return new OrderItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("some_title")
                        .setIsbn("some_isbn")
                        .setPrice(BigDecimal.valueOf(99.99)))
                .setPrice(BigDecimal.valueOf(99.99))
                .setQuantity(1);

    }

    public static OrderItemDto validOrderItemDto() {
        return new OrderItemDto(1L, 1L, 1);
    }
}
