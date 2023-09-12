package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import mate.academy.bookstore.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(
            UserRegistrationRequestDto request
    ) throws RegistrationException;
}
