package mate.academy.bookstore.dto.cartitem;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequestDto(@Min(1) int quantity) {
}
