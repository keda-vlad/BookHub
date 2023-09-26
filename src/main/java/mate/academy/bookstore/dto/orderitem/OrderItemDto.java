package mate.academy.bookstore.dto.orderitem;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity
) {
}
