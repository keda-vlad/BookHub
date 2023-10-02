package mate.academy.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderException;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Verify placeAnOrder() method works for OrderServiceImpl")
    void placeAnOrder_ValidUserIdAndRequestDto_ReturnOrderDto() {
        OrderDto orderDto = new OrderDto(
                1L,
                1L,
                Set.of(new OrderItemDto(1L, 1L, 1)),
                null,
                BigDecimal.valueOf(1),
                Order.Status.PENDING
        );
        ShoppingCart shoppingCart = new ShoppingCart()
                .setCartItems(Set.of(
                        new CartItem()
                                .setQuantity(1)
                                .setBook(new Book()
                                        .setPrice(BigDecimal.valueOf(10)))
                ));
        PlaceAnOrderDto placeAnOrderDto = new PlaceAnOrderDto("some_address");
        Mockito.when(shoppingCartRepository.findByUserId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(new Order());
        Mockito.when(orderMapper.toDto(Mockito.any(Order.class)))
                .thenReturn(orderDto);

        OrderDto actual = orderService.placeAnOrder(1L, placeAnOrderDto);

        Assertions.assertEquals(orderDto, actual);
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserId(ArgumentMatchers.anyLong());
        Mockito.verify(orderRepository, Mockito.times(1))
                .save(Mockito.any(Order.class));
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .deleteAll(Mockito.any());
        Mockito.verify(orderMapper, Mockito.times(1))
                .toDto(Mockito.any(Order.class));
        Mockito.verifyNoMoreInteractions(shoppingCartRepository, orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify the expected placeAnOrder() exception occurs for OrderServiceImpl")
    void placeAnOrder_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(shoppingCartRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () ->
                        orderService.placeAnOrder(100L, Mockito.any()));

        //Then
        Assertions.assertEquals("Can't find shopping cart by user id = 100",
                exception.getMessage());
        Mockito.verify(shoppingCartRepository, Mockito.times(1)).findByUserId(Mockito.any());
        Mockito.verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify the expected placeAnOrder() exception occurs for OrderServiceImpl")
    void placeAnOrder_EmptyShoppingCart_ThrowOrderException() {
        //Given
        Mockito.when(shoppingCartRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new ShoppingCart()));

        //When
        Exception exception = Assertions.assertThrows(
                OrderException.class, () -> orderService.placeAnOrder(100L, Mockito.any()));

        //Then
        Assertions.assertEquals("Can't create empty order", exception.getMessage());
        Mockito.verify(shoppingCartRepository, Mockito.times(1)).findByUserId(Mockito.any());
        Mockito.verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify getAllOrders() method works for OrderServiceImpl")
    void getAllOrders_ValidId_ReturnListOrderDto() {
        OrderDto orderDto = new OrderDto(
                1L, 1L, Set.of(), null, null, null
        );
        Mockito.when(orderRepository.findAllByUserId(ArgumentMatchers.anyLong(), Mockito.any()))
                .thenReturn(List.of(new Order(), new Order(), new Order()));
        Mockito.when(orderMapper.toDto(Mockito.any(Order.class)))
                .thenReturn(orderDto);
        List<OrderDto> expected = List.of(orderDto, orderDto, orderDto);
        List<OrderDto> actual = orderService.getAllOrders(1L, null);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(orderRepository, Mockito.times(1))
                .findAllByUserId(ArgumentMatchers.anyLong(), Mockito.any());
        Mockito.verify(orderMapper, Mockito.times(expected.size()))
                .toDto(Mockito.any(Order.class));
        Mockito.verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify updateOrderStatus() method works for OrderServiceImpl")
    void updateOrderStatus_ValidIdAndRequestDto_ReturnOrderDto() {
        ChangeStatusDto changeStatusDto = new ChangeStatusDto(Order.Status.DELIVERED.name());
        OrderDto orderDto = new OrderDto(
                1L, 1L, Set.of(), null, BigDecimal.valueOf(1), Order.Status.DELIVERED
        );
        Mockito.when(orderRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Order()));
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(new Order());
        Mockito.when(orderMapper.toDto(Mockito.any(Order.class)))
                .thenReturn(orderDto);

        OrderDto actual = orderService.updateOrderStatus(1L, changeStatusDto);

        Assertions.assertEquals(orderDto, actual);
        Mockito.verify(orderRepository, Mockito.times(1)).findById(ArgumentMatchers.anyLong());
        Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(orderMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify the expected updateOrderStatus() exception occurs for OrderServiceImpl")
    void updateOrderStatus_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(orderRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () ->
                        orderService.updateOrderStatus(100L, Mockito.any()));

        //Then
        Assertions.assertEquals("Can't find order by id = 100", exception.getMessage());
        Mockito.verify(orderRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(orderRepository);
    }
}
