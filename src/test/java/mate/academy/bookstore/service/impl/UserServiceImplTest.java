package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestUserProvider.validRegistrationRequest;
import static mate.academy.bookstore.util.TestUserProvider.validRegistrationResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify register() method works for UserServiceImpl")
    void registerUser_ValidRequestDto_ReturnUserRegistrationResponseDto()
            throws RegistrationException {
        UserRegistrationRequestDto requestDto = validRegistrationRequest();
        UserRegistrationResponseDto responseDto = validRegistrationResponse();

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("123");
        when(roleRepository.getByName(any())).thenReturn(mock(Role.class));
        when(userRepository.save(any(User.class))).thenReturn(mock(User.class));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        UserRegistrationResponseDto registered = userService.register(requestDto);

        assertEquals(responseDto, registered);
        verify(userRepository).findByEmail(any());
        verify(passwordEncoder).encode(any());
        verify(roleRepository).getByName(any());
        verify(userRepository).save(any());
        verify(userMapper).toResponseDto(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder, roleRepository, userMapper);
    }

    @Test
    @DisplayName("Verify the expected register() exception occurs for UserServiceImpl")
    public void registerUser_InvalidRequestDto_ThrowRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                null, null, null,
                null, null, null
        );
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(
                RegistrationException.class, () -> userService.register(requestDto));

        assertEquals("Unable to complete registration", exception.getMessage());
        verify(userRepository).findByEmail(any());
        verifyNoMoreInteractions(userRepository);
    }
}
