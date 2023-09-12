package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.bookstore.validation.FieldsMatch;

@FieldsMatch(field = "password", fieldMatch = "repeatPassword")
@Data
public final class UserRegistrationRequestDto {
    private final @Email String email;
    private final @NotBlank @Size(min = 4, max = 50) String password;
    private final @NotBlank String repeatPassword;
    private final @NotBlank String firstName;
    private final @NotBlank String lastName;
    private final String shippingAddress;
}
