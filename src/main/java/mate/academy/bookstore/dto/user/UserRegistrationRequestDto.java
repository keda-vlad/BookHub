package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mate.academy.bookstore.validation.FieldsMatch;

@FieldsMatch(field = "password", fieldMatch = "repeatPassword")
public record UserRegistrationRequestDto(
        @Email String email,
        @NotBlank @Size(min = 4, max = 50) String password,
        @NotBlank String repeatPassword,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String shippingAddress
) {
}
