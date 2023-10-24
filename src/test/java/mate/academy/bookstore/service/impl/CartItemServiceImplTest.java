package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestShoppingCartProvider.validCartItem;
import static mate.academy.bookstore.util.TestShoppingCartProvider.validUpdateCartItemDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.UpdateQuantityRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        CartItem cartItem = validCartItem();
        UpdateQuantityRequestDto requestDto = new UpdateQuantityRequestDto(3);
        CartItemDto cartItemDto = validUpdateCartItemDto();

        when(cartItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        CartItemDto updatedCartItemDto = cartItemService.update(requestDto, anyLong());

        assertEquals(cartItemDto, updatedCartItemDto);
        verify(cartItemMapper).toDto(any());
        verify(cartItemRepository).save(any());
        verify(cartItemRepository).findById(any());
        verifyNoMoreInteractions(cartItemMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Verify the expected update() exception occurs for CartItemServiceImpl")
    public void getCartItem_InvalidId_ThrowEntityNotFoundException() {
        when(cartItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> cartItemService.update(any(),100L));

        assertEquals("Can't find cart item by id = 100", exception.getMessage());
        verify(cartItemRepository, times(1)).findById(any());
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for CartItemServiceImpl")
    public void deleteCartItem_ValidId() {
        Long id = 1L;
        cartItemService.delete(id);
        verify(cartItemRepository, times(1))
                .deleteById(anyLong());
        verifyNoMoreInteractions(cartItemRepository);
    }
}
