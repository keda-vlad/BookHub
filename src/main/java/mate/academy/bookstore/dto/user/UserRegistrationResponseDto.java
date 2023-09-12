package mate.academy.bookstore.dto.user;

import lombok.Data;

@Data
public final class UserRegistrationResponseDto {
    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String shippingAddress;
}
