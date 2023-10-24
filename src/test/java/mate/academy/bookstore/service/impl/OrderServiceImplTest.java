package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestOrderProvider.validDeliveredOrderDto;
import static mate.academy.bookstore.util.TestOrderProvider.validOrderDto;
import static mate.academy.bookstore.util.TestShoppingCartProvider.validShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderException;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        OrderDto orderDto = validOrderDto();
        ShoppingCart shoppingCart = validShoppingCart();
        PlaceAnOrderDto placeAnOrderDto = new PlaceAnOrderDto("some_address");
        when(shoppingCartRepository.findByUserId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(shoppingCart));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto actual = orderService.placeAnOrder(1L, placeAnOrderDto);

        assertEquals(orderDto, actual);
        verify(shoppingCartRepository).findByUserId(ArgumentMatchers.anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(cartItemRepository).deleteAll(any());
        verify(orderMapper).toDto(any(Order.class));
        verifyNoMoreInteractions(shoppingCartRepository, orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify the expected placeAnOrder() exception occurs for OrderServiceImpl")
    void placeAnOrder_InvalidId_ThrowEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () ->
                        orderService.placeAnOrder(100L, any()));

        assertEquals("Can't find shopping cart by user id = 100",
                exception.getMessage());
        verify(shoppingCartRepository, times(1)).findByUserId(any());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify the expected placeAnOrder() exception occurs for OrderServiceImpl")
    void placeAnOrder_EmptyShoppingCart_ThrowOrderException() {
        when(shoppingCartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(new ShoppingCart()));

        Exception exception = assertThrows(
                OrderException.class, () -> orderService.placeAnOrder(100L, any()));

        assertEquals("Can't create empty order", exception.getMessage());
        verify(shoppingCartRepository, times(1)).findByUserId(any());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify getAllOrders() method works for OrderServiceImpl")
    void getAllOrders_ValidId_ReturnListOrderDto() {
        OrderDto orderDto = validOrderDto();
        when(orderRepository.findAllByUserId(ArgumentMatchers.anyLong(), any()))
                .thenReturn(List.of(new Order(), new Order(), new Order()));
        when(orderMapper.toDto(any(Order.class)))
                .thenReturn(orderDto);
        List<OrderDto> expected = List.of(orderDto, orderDto, orderDto);
        List<OrderDto> actual = orderService.getAllOrders(1L, null);

        assertEquals(expected, actual);
        verify(orderRepository).findAllByUserId(ArgumentMatchers.anyLong(), any());
        verify(orderMapper, times(expected.size()))
                .toDto(any(Order.class));
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify updateOrderStatus() method works for OrderServiceImpl")
    void updateOrderStatus_ValidIdAndRequestDto_ReturnOrderDto() {
        ChangeStatusDto changeStatusDto = new ChangeStatusDto(Order.Status.DELIVERED.name());
        OrderDto orderDto = validDeliveredOrderDto();
        when(orderRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Order()));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(new Order());
        when(orderMapper.toDto(any(Order.class)))
                .thenReturn(orderDto);

        OrderDto actual = orderService.updateOrderStatus(1L, changeStatusDto);

        assertEquals(orderDto, actual);
        verify(orderRepository).findById(ArgumentMatchers.anyLong());
        verify(orderRepository).save(any());
        verify(orderMapper).toDto(any());
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify the expected updateOrderStatus() exception occurs for OrderServiceImpl")
    void updateOrderStatus_InvalidId_ThrowEntityNotFoundException() {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () ->
                        orderService.updateOrderStatus(100L, any()));

        assertEquals("Can't find order by id = 100", exception.getMessage());
        verify(orderRepository, times(1)).findById(any());
        verifyNoMoreInteractions(orderRepository);
    }
}
