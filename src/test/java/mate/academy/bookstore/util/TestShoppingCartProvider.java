package mate.academy.bookstore.util;

import java.math.BigDecimal;
import java.util.Set;
import mate.academy.bookstore.dto.cartitem.AddToCartRequestDto;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;

public class TestShoppingCartProvider {
    public static ShoppingCartDto validShoppingCartDto() {
        return new ShoppingCartDto(
                17L,
                17L,
                Set.of(new CartItemDto(1L, 1L, "some_title", 10))
        );
    }

    public static AddToCartRequestDto validAddToCartRequestDto() {
        return new AddToCartRequestDto(
                1L,
                10
        );
    }

    public static ShoppingCart validShoppingCart() {
        return new ShoppingCart()
                .setId(17L)
                .setCartItems(Set.of(
                        new CartItem()
                                .setQuantity(10)
                                .setBook(new Book()
                                        .setPrice(BigDecimal.valueOf(10)))
                ));
    }

    public static CartItem validCartItem() {
        return new CartItem()
                .setQuantity(10)
                .setId(1L);
    }

    public static ShoppingCart validBigShoppingCart() {
        return new ShoppingCart()
                .setId(17L)
                .setUser(new User()
                        .setId(17L)
                        .setEmail("some_email@exam.com")
                        .setPassword("Password")
                        .setFirstName("FirstName")
                        .setLastName("SecondName"))
                .setCartItems(Set.of(
                        new CartItem()
                                .setId(1L)
                                .setQuantity(10)
                                .setBook(new Book()
                                        .setId(1L)
                                        .setTitle("some_title")
                                        .setIsbn("some_isbn")
                                        .setPrice(BigDecimal.valueOf(99.99)))));
    }

    public static CartItemDto validUpdateCartItemDto() {
        return new CartItemDto(
                1L,
                null,
                null,
                10);
    }
}
