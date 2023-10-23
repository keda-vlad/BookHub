package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestBookProvider.validBook;
import static mate.academy.bookstore.util.TestShoppingCartProvider.validAddToCartRequestDto;
import static mate.academy.bookstore.util.TestShoppingCartProvider.validShoppingCart;
import static mate.academy.bookstore.util.TestShoppingCartProvider.validShoppingCartDto;
import static mate.academy.bookstore.util.TestUserProvider.validUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.cartitem.AddToCartRequestDto;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    @DisplayName("Verify getById() method works for ShoppingCartServiceImpl")
    public void getShoppingCartById_ValidId_ReturnShoppingCartDto() {
        Long id = 17L;
        ShoppingCart shoppingCart = validShoppingCart();
        ShoppingCartDto shoppingCartDto = validShoppingCartDto();

        when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService.getById(id);

        assertEquals(shoppingCartDto, actualShoppingCartDto);
        verify(shoppingCartMapper, times(1)).toDto(any());
        verify(shoppingCartRepository, times(1)).findByUserId(any());
        verifyNoMoreInteractions(shoppingCartMapper, shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for ShoppingCartServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.getById(100L));

        assertEquals("Can't find shopping cart by id = 100", exception.getMessage());
        verify(shoppingCartRepository, times(1)).findByUserId(any());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
    Verify addToCart() method works for ShoppingCartServiceImpl
    add new cartItem to existed shopping cart
                """)
    public void updateShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        Long id = 12L;
        User user = validUser();
        Book book = validBook();
        AddToCartRequestDto addToCartRequestDto = validAddToCartRequestDto();
        ShoppingCart shoppingCart = validShoppingCart();
        ShoppingCartDto shoppingCartDto = validShoppingCartDto();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(any()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(any()))
                .thenReturn(Optional.of(book));
        when(cartItemRepository.save(any()))
                .thenReturn(any());
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        assertEquals(shoppingCartDto, actualShoppingCartDto);
        assertTrue(shoppingCartDto.cartItems().stream().findFirst().isPresent());
        assertEquals(
                10,
                shoppingCartDto.cartItems().stream().findFirst().get().quantity()
        );
        verify(userRepository).findById(any());
        verify(shoppingCartRepository).findByUserId(anyLong());
        verify(bookRepository).findById(anyLong());
        verify(cartItemRepository).save(any());
        verify(shoppingCartMapper).toDto(any());
        verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("""
    Verify addToCart() method works for ShoppingCartServiceImpl
    create new shopping cart
                """)
    public void createShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        Long id = 17L;
        User user = validUser();
        Book book = validBook();
        AddToCartRequestDto addToCartRequestDto = validAddToCartRequestDto();
        ShoppingCart shoppingCart = validShoppingCart();
        ShoppingCartDto shoppingCartDto = validShoppingCartDto();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any()))
                .thenReturn(shoppingCart);
        when(bookRepository.findById(anyLong()))
                .thenReturn(Optional.of(book));
        when(cartItemRepository.save(any()))
                .thenReturn(any());
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        assertEquals(shoppingCartDto, actualShoppingCartDto);
        verify(userRepository).findById(any());
        verify(userRepository).findById(any());
        verify(shoppingCartRepository).findByUserId(anyLong());
        verify(bookRepository).findById(anyLong());
        verify(cartItemRepository).save(any());
        verify(shoppingCartMapper).toDto(any());
        verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("""
                    Verify addToCart() method works for ShoppingCartServiceImpl
                    update quantity in cart item if book already in cart
                    """)
    public void updateCartItemInShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        Long id = 1L;
        User user = validUser();
        Book book = validBook();
        AddToCartRequestDto addToCartRequestDto = validAddToCartRequestDto();
        ShoppingCart shoppingCart = validShoppingCart();
        ShoppingCartDto shoppingCartDto = validShoppingCartDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(any())).thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(new CartItem());
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(shoppingCartDto);

        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        assertEquals(shoppingCartDto, actualShoppingCartDto);
        verify(userRepository).findById(any());
        verify(userRepository).findById(any());
        verify(shoppingCartRepository).findByUserId(anyLong());
        verify(bookRepository).findById(anyLong());
        verify(cartItemRepository).save(any());
        verify(shoppingCartMapper).toDto(any());
        verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify the expected addToCart() exception occurs for ShoppingCartServiceImpl")
    public void addToCart_InvalidUserId_ThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.addToCart(null, 100L));

        assertEquals("Can't find user by id = 100", exception.getMessage());
        verify(userRepository, times(1)).findById(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for ShoppingCartServiceImpl")
    public void addToCart_InvalidBookId_ThrowEntityNotFoundException() {
        Long id = 1L;
        User user = validUser();
        ShoppingCart shoppingCart = validShoppingCart();
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto(
                100L,
                10
        );

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(any()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(any()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () ->
                        shoppingCartService.addToCart(addToCartRequestDto, 1L)
        );

        assertEquals("Can't find book by id = 100", exception.getMessage());
        verify(userRepository, times(1))
                .findById(any());
        verify(shoppingCartRepository, times(1))
                .findByUserId(anyLong());
        verify(bookRepository, times(1))
                .findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
