package mate.academy.bookstore.service.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderException;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public OrderDto placeAnOrder(Long userId, PlaceAnOrderDto placeAnOrderDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find shopping cart by user id = " + userId));
        Set<OrderItem> orderItems = buildOrderItems(shoppingCart);
        Order order = buildOrder(shoppingCart, placeAnOrderDto, orderItems);
        orderRepository.save(order);
        orderItems.forEach(oi -> oi.setOrder(order));
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders(Long id, Pageable pageable) {
        return orderRepository.findAllByUserId(id, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto updateOrderStatus(Long id, ChangeStatusDto changeStatusDto) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id = " + id));
        order.setStatus(Order.Status.valueOf(changeStatusDto.status()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order buildOrder(
            ShoppingCart shoppingCart,
            PlaceAnOrderDto placeAnOrderDto,
            Set<OrderItem> orderItems
    ) {
        if (orderItems.isEmpty()) {
            throw new OrderException("Can't create empty order");
        }
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setUser(shoppingCart.getUser());
        order.setTotal(calculateTotalPrice(orderItems));
        order.setOrderItems(orderItems);
        order.setShippingAddress(placeAnOrderDto.shippingAddress());
        return order;
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal::add)
                .orElseGet(() -> BigDecimal.valueOf(0L));
    }

    private Set<OrderItem> buildOrderItems(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(this::buildOrderItem)
                .collect(Collectors.toSet());
    }

    private OrderItem buildOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(cartItem.getBook().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setBook(cartItem.getBook());
        return orderItem;
    }

}
