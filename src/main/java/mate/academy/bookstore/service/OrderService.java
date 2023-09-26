package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeAnOrder(Long userId, PlaceAnOrderDto placeAnOrderDto);

    List<OrderDto> getAllOrders(Long id, Pageable pageable);

    OrderDto updateOrderStatus(Long id, ChangeStatusDto changeStatusDto);
}
