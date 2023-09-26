package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {
    List<OrderItemDto> findAllByOrderId(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getByOrderIdAndItemId(Long userId, Long orderId, Long itemId);
}
