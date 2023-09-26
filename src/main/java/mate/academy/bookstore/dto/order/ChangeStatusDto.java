package mate.academy.bookstore.dto.order;

import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.validation.EnumValue;

public record ChangeStatusDto(
        @EnumValue(enumClass = Order.Status.class)
        String status
) {
}
