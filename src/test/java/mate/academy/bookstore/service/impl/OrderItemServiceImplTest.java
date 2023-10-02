package mate.academy.bookstore.service.impl;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        OrderItemDto orderItemDto = new OrderItemDto(1L, 1L, 1);
        List<OrderItemDto> expected = List.of(orderItemDto, orderItemDto, orderItemDto);

        Mockito.when(orderItemRepository.findAllByUserIdAndOrderId(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(new OrderItem(), new OrderItem(), new OrderItem()));
        Mockito.when(orderItemMapper.toDto(Mockito.any(OrderItem.class)))
                .thenReturn(orderItemDto);

        List<OrderItemDto> actual = orderItemService
                .findAllByOrderId(1L, 1L, Pageable.unpaged());

        Assertions.assertEquals(actual, expected);
        Mockito.verify(orderItemRepository, Mockito.times(1))
                .findAllByUserIdAndOrderId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verify(orderItemMapper, Mockito.times(expected.size())).toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName("Verify getByOrderIdAndItemId() method works for OrderItemServiceImpl")
    void getByOrderIdAndItemId_ValidIds_ReturnOrderItemDto() {
        OrderItemDto orderItemDto = new OrderItemDto(1L, 1L, 1);
        Mockito.when(orderItemRepository.findByUserIdAndItemIdAndOrderId(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong())
        ).thenReturn(Optional.of(new OrderItem()));
        Mockito.when(orderItemMapper.toDto(Mockito.any(OrderItem.class))
        ).thenReturn(orderItemDto);

        OrderItemDto byOrderIdAndItemId = orderItemService.getByOrderIdAndItemId(1L, 1L, 1L);

        Assertions.assertNotNull(byOrderIdAndItemId);
        Mockito.verify(orderItemRepository, Mockito.times(1))
                .findByUserIdAndItemIdAndOrderId(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()
                );
        Mockito.verify(orderItemMapper, Mockito.times(1))
                .toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName(
            "Verify the expected getByOrderIdAndItemId() exception occurs for OrderItemServiceImpl"
    )
    public void getByOrderIdAndItemId_InvalidIds_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(orderItemRepository.findByUserIdAndItemIdAndOrderId(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.anyLong())
                ).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () ->
                        orderItemService.getByOrderIdAndItemId(100L, 100L, 100L));

        //Then
        Assertions.assertEquals(
                "Can't find order item by itemId = 100 and orderId = 100",
                exception.getMessage()
        );
        Mockito.verify(orderItemRepository, Mockito.times(1))
                .findByUserIdAndItemIdAndOrderId(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.anyLong()
                );
        Mockito.verifyNoMoreInteractions(orderItemRepository);
    }
}
