package mate.academy.bookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.service.OrderItemService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> findAllByOrderId(Long userId, Long orderId, Pageable pageable) {
        return orderItemRepository.findAllByUserIdAndOrderId(userId, orderId, pageable).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getByOrderIdAndItemId(Long userId, Long orderId, Long itemId) {
        OrderItem orderItemDto = orderItemRepository
                .findByUserIdAndItemIdAndOrderId(userId, itemId, orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order item by itemId = " + itemId
                        + " and orderId = " + orderId)
        );
        return orderItemMapper.toDto(orderItemDto);
    }
}
