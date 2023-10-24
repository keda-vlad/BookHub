package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestOrderProvider.validOrderItemDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemMapper;
    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    @DisplayName("Verify findAllByOrderId() method works for OrderItemServiceImpl")
    void findAllOrderItemByOrderId_ValidUserIdAndOrderId_ReturnListOrderItemDto() {
        OrderItemDto orderItemDto = validOrderItemDto();
        List<OrderItemDto> expected = List.of(orderItemDto, orderItemDto, orderItemDto);

        when(orderItemRepository.findAllByUserIdAndOrderId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(new OrderItem(), new OrderItem(), new OrderItem()));
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(orderItemDto);

        List<OrderItemDto> actual = orderItemService
                .findAllByOrderId(1L, 1L, Pageable.unpaged());

        assertEquals(actual, expected);
        verify(orderItemRepository).findAllByUserIdAndOrderId(anyLong(), anyLong(), any());
        verify(orderItemMapper, times(expected.size())).toDto(any());
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName("Verify getByOrderIdAndItemId() method works for OrderItemServiceImpl")
    void getByOrderIdAndItemId_ValidIds_ReturnOrderItemDto() {
        OrderItemDto orderItemDto = validOrderItemDto();
        when(orderItemRepository.findByUserIdAndItemIdAndOrderId(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.of(new OrderItem()));
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(orderItemDto);

        OrderItemDto byOrderIdAndItemId = orderItemService.getByOrderIdAndItemId(1L, 1L, 1L);

        assertNotNull(byOrderIdAndItemId);
        verify(orderItemRepository)
                .findByUserIdAndItemIdAndOrderId(anyLong(), anyLong(), anyLong());
        verify(orderItemMapper).toDto(any());
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName(
            "Verify the expected getByOrderIdAndItemId() exception occurs for OrderItemServiceImpl"
    )
    public void getByOrderIdAndItemId_InvalidIds_ThrowEntityNotFoundException() {
        when(orderItemRepository.findByUserIdAndItemIdAndOrderId(
                anyLong(),
                anyLong(),
                anyLong())
                ).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () ->
                        orderItemService.getByOrderIdAndItemId(100L, 100L, 100L));

        assertEquals(
                "Can't find order item by itemId = 100 and orderId = 100",
                exception.getMessage()
        );
        verify(orderItemRepository, times(1))
                .findByUserIdAndItemIdAndOrderId(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );
        verifyNoMoreInteractions(orderItemRepository);
    }
}
