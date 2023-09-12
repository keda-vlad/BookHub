package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    private final @NotEmpty @Size(min = 8, max = 20) @Email String email;
    private final @NotEmpty @Size(min = 8, max = 20) String password;
}
