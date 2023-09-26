package mate.academy.bookstore.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequestDto(
        @NotNull Long bookId,
        @Min(1) int quantity
) {
}
