package mate.academy.bookstore.service.impl;

import java.util.Optional;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        //Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "some_new_email",
                null,
                null,
                null,
                null,
                null
        );
        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                1L,
                "some_new_email",
                null,
                null,
                null
        );

        Mockito.when(userRepository.findByEmail(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.any()))
                .thenReturn("123");
        Mockito.when(roleRepository.getByName(Mockito.any()))
                .thenReturn(Mockito.mock(Role.class));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(Mockito.mock(User.class));
        Mockito.when(userMapper.toResponseDto(Mockito.any(User.class)))
                .thenReturn(responseDto);
        //When
        UserRegistrationResponseDto registered = userService.register(requestDto);
        //Then
        Assertions.assertEquals(responseDto, registered);
        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.times(1))
                .encode(Mockito.any());
        Mockito.verify(roleRepository, Mockito.times(1))
                .getByName(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verify(userMapper, Mockito.times(1))
                .toResponseDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(
                userRepository, passwordEncoder, roleRepository, userMapper
        );
    }

    @Test
    @DisplayName("Verify the expected register() exception occurs for UserServiceImpl")
    public void registerUser_InvalidRequestDto_ThrowRegistrationException() {
        //Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                null, null, null,
                null, null, null
        );
        Mockito.when(userRepository.findByEmail(Mockito.any()))
                .thenReturn(Optional.of(new User()));

        //When
        Exception exception = Assertions.assertThrows(
                RegistrationException.class, () -> userService.register(requestDto));

        //Then
        Assertions.assertEquals("Unable to complete registration", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.any());
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
