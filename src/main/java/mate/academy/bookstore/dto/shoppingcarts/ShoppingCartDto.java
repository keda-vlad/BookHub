package mate.academy.bookstore.dto.shoppingcarts;

import java.util.Set;
import mate.academy.bookstore.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemDto> cartItems
) {
}
