package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.AddToCartRequestDto;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getById(Long id);

    ShoppingCartDto addToCart(AddToCartRequestDto addToCartRequestDto, Long userId);
}
