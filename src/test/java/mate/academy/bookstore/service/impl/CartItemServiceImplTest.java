package mate.academy.bookstore.service.impl;

import java.util.Optional;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.UpdateQuantityRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Test
    @DisplayName("Verify update() method works for CartItemServiceImpl")
    public void updateCartItem_ValidRequestDto_ReturnCartItemDto() {
        //Given
        CartItem cartItem = new CartItem()
                .setQuantity(10)
                .setId(1L);
        UpdateQuantityRequestDto requestDto = new UpdateQuantityRequestDto(3);
        CartItemDto cartItemDto = new CartItemDto(
                cartItem.getId(),
                null,
                null,
                cartItem.getQuantity());

        Mockito.when(cartItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        Mockito.when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        //When
        CartItemDto updatedCartItemDto = cartItemService.update(requestDto, Mockito.anyLong());

        //Then
        Assertions.assertEquals(cartItemDto, updatedCartItemDto);
        Mockito.verify(cartItemMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(cartItemRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(cartItemRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(cartItemMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Verify the expected update() exception occurs for CartItemServiceImpl")
    public void getCartItem_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(cartItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> cartItemService.update(Mockito.any(),100L));

        //Then
        Assertions.assertEquals("Can't find cart item by id = 100", exception.getMessage());
        Mockito.verify(cartItemRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for CartItemServiceImpl")
    public void deleteCartItem_ValidId() {
        //Given
        Long id = 1L;

        //When
        cartItemService.delete(id);

        //Then
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(cartItemRepository);
    }
}
