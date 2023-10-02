package mate.academy.bookstore.service.impl;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.cartitem.AddToCartRequestDto;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        //Given
        Long id = 1L;
        ShoppingCart shoppingCart = new ShoppingCart().setId(id);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(id, id, Set.of());

        Mockito.when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService.getById(id);

        //Then
        Assertions.assertEquals(shoppingCartDto, actualShoppingCartDto);
        Mockito.verify(shoppingCartMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(shoppingCartRepository, Mockito.times(1)).findByUserId(Mockito.any());
        Mockito.verifyNoMoreInteractions(shoppingCartMapper, shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for ShoppingCartServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(shoppingCartRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.getById(100L));

        //Then
        Assertions.assertEquals("Can't find shopping cart by id = 100", exception.getMessage());
        Mockito.verify(shoppingCartRepository, Mockito.times(1)).findByUserId(Mockito.any());
        Mockito.verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
    Verify addToCart() method works for ShoppingCartServiceImpl
    add new cartItem to existed shopping cart
                """)
    public void updateShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        //Given
        Long id = 1L;
        User user = new User().setId(id);
        Book book = new Book().setId(id);
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto(
                book.getId(),
                10
        );
        CartItem cartItem = new CartItem().setId(id)
                .setBook(book)
                .setQuantity(addToCartRequestDto.quantity());
        CartItemDto cartItemDto = new CartItemDto(
                cartItem.getId(),
                book.getId(),
                book.getTitle(),
                10
        );

        ShoppingCart shoppingCart = new ShoppingCart().setId(id).setCartItems(Set.of(cartItem));
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(
                id, user.getId(), Set.of(cartItemDto)
        );

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));
        Mockito.when(cartItemRepository.save(Mockito.any()))
                .thenReturn(Mockito.any());
        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        //Then
        Assertions.assertEquals(shoppingCartDto, actualShoppingCartDto);
        Assertions.assertTrue(shoppingCartDto.cartItems().stream().findFirst().isPresent());
        Assertions.assertEquals(
                10,
                shoppingCartDto.cartItems().stream().findFirst().get().quantity()
        );
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserId(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(shoppingCartMapper, Mockito.times(1))
                .toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("""
    Verify addToCart() method works for ShoppingCartServiceImpl
    create new shopping cart
                """)
    public void createShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        //Given
        Long id = 1L;
        User user = new User().setId(id);
        Book book = new Book().setId(id);
        ShoppingCart shoppingCart = new ShoppingCart().setId(id);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(id, user.getId(), Set.of());
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto(
                book.getId(),
                10
        );
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.empty());
        Mockito.when(shoppingCartRepository.save(Mockito.any()))
                .thenReturn(shoppingCart);
        Mockito.when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));
        Mockito.when(cartItemRepository.save(Mockito.any()))
                .thenReturn(Mockito.any());
        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        //Then
        Assertions.assertEquals(shoppingCartDto, actualShoppingCartDto);
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserId(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(shoppingCartMapper, Mockito.times(1))
                .toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("""
                    Verify addToCart() method works for ShoppingCartServiceImpl
                    update quantity in cart item if book already in cart
                    """)
    public void updateCartItemInShoppingCart_ValidIdAndRequestDto_ReturnShoppingCartDto() {
        //Given
        Long id = 1L;
        User user = new User().setId(id);
        Book book = new Book().setId(id);
        ShoppingCart shoppingCart = new ShoppingCart().setId(id);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(id, user.getId(), Set.of());
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto(
                book.getId(),
                10
        );
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.empty());
        Mockito.when(shoppingCartRepository.save(Mockito.any()))
                .thenReturn(shoppingCart);
        Mockito.when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));
        Mockito.when(cartItemRepository.save(Mockito.any()))
                .thenReturn(Mockito.any());
        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService
                .addToCart(addToCartRequestDto, id);

        //Then
        Assertions.assertEquals(shoppingCartDto, actualShoppingCartDto);
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserId(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(shoppingCartMapper, Mockito.times(1))
                .toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(
                userRepository, cartItemRepository, shoppingCartMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify the expected addToCart() exception occurs for ShoppingCartServiceImpl")
    public void addToCart_InvalidUserId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.addToCart(null, 100L));

        //Then
        Assertions.assertEquals("Can't find user by id = 100", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for ShoppingCartServiceImpl")
    public void addToCart_InvalidBookId_ThrowEntityNotFoundException() {
        //Given
        Long id = 1L;
        User user = new User().setId(id);
        ShoppingCart shoppingCart = new ShoppingCart().setId(id);
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto(
                100L,
                10
        );

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(id))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(bookRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        //When

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () ->
                        shoppingCartService.addToCart(addToCartRequestDto, 1L)
        );

        //Then
        Assertions.assertEquals("Can't find book by id = 100", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .findByUserId(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
