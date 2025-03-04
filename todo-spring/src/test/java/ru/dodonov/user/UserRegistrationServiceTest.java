package ru.dodonov.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.dodonov.user.api.SignInUpRequest;
import ru.dodonov.user.db.UserEntity;
import ru.dodonov.user.db.UserEntityMapper;
import ru.dodonov.user.db.UserRepository;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.domain.UserRegistrationService;
import ru.dodonov.user.domain.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEntityMapper entityMapper;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void registerUser_ValidRequest_ReturnsUser() {
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        SignInUpRequest request = new SignInUpRequest("testUser", rawPassword);

        UserEntity expectedEntityToSave = new UserEntity(
                null,
                request.login(),
                UserRole.USER,
                encodedPassword
        );

        UserEntity savedEntity = new UserEntity(
                1L,
                request.login(),
                UserRole.USER,
                encodedPassword
        );

        User expectedUser = new User(1L,
                "testUser",
                UserRole.USER,
                "encodedPassword"
        );

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(expectedUser);

        User result = userRegistrationService.registerUser(request);

        assertNotNull(result);
        assertEquals(expectedUser, result);

        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(expectedEntityToSave);
        verify(entityMapper).toDomain(savedEntity);
    }
}
