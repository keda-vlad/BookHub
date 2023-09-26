package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.UpdateQuantityRequestDto;

public interface CartItemService {
    CartItemDto update(UpdateQuantityRequestDto updateQuantityRequestDto, Long id);

    void delete(Long id);
}
