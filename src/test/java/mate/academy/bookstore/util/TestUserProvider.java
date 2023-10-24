package mate.academy.bookstore.util;

import java.util.Set;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class TestUserProvider {
    public static UserRegistrationRequestDto validRegistrationRequest() {
        return new UserRegistrationRequestDto(
                "somenew@example.com",
                "someNewPassword",
                "someNewPassword",
                "someFirstName",
                "someLastName",
                "someAddress");
    }

    public static UserRegistrationResponseDto validRegistrationResponse() {
        return new UserRegistrationResponseDto(
                12L,
                "somenew@example.com",
                "someFirstName",
                "someLastName",
                "someAddress"
        );
    }

    public static User validUser() {
        return new User()
                .setId(12L)
                .setEmail("somenew@example.com")
                .setFirstName("FirstName")
                .setLastName("SecondName")
                .setPassword("Password");
    }

    public static Authentication authentication() {
        User user = new User()
                .setId(17L)
                .setEmail("some_email@exam.com")
                .setRoles(Set.of(
                        new Role()
                                .setName(Role.RoleName.ROLE_ADMIN)
                                .setId(2L)));
        return new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
