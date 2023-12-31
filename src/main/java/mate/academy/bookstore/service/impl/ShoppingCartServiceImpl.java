package mate.academy.bookstore.service.impl;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getById(Long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart by id = " + id));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addToCart(AddToCartRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by id = " + userId));
        ShoppingCart shoppingCart = getOrCreateShoppingCart(user);
        updateOrAddCartItem(shoppingCart, requestDto);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart getOrCreateShoppingCart(User user) {
        return shoppingCartRepository.findByUserId(user.getId()).orElseGet(() -> {
            ShoppingCart newShoppingCart = new ShoppingCart();
            newShoppingCart.setUser(user);
            return shoppingCartRepository.save(newShoppingCart);
        });
    }

    private void updateOrAddCartItem(
            ShoppingCart shoppingCart,
            AddToCartRequestDto requestDto
    ) {
        Book book = bookRepository.findById(requestDto.bookId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find book by id = " + requestDto.bookId()));
        Optional<CartItem> optionalCartItem = shoppingCart.getCartItems().stream()
                .filter(ci -> Objects.equals(ci.getBook().getId(), book.getId()))
                .findFirst();
        CartItem cartItem;
        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.quantity());
        } else {
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setQuantity(requestDto.quantity());
            shoppingCart.getCartItems().add(cartItem);
        }
        cartItemRepository.save(cartItem);
    }
}
