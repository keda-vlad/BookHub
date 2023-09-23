package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cartitem.AddToCartRequestDto;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.UpdateQuantityRequestDto;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.service.CartItemService;
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
@Tag(name = "Shopping carts management", description = "Endpoint for managing shopping carts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Get a shopping cart for authenticated user")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getById(user.getId());
    }

    @PostMapping
    @Operation(summary = "Add book to shopping cart", description = "Add book to shopping cart")
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid AddToCartRequestDto addToCartRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addToCart(addToCartRequestDto, user.getId());
    }

    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update shopping cart",
            description = "Update quantity of books in shopping cart")
    public CartItemDto updateQuantityOfBookInShoppingCart(
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateQuantityRequestDto updateQuantityRequestDto
    ) {
        return cartItemService.update(updateQuantityRequestDto, cartItemId);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete shopping cart", description = "Delete shopping by id")
    public void removeBookFromShoppingCart(@PathVariable Long cartItemId) {
        cartItemService.delete(cartItemId);
    }
}
