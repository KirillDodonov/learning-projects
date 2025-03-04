package ru.dodonov.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.dodonov.security.JwtTokenManager;
import ru.dodonov.user.api.SignInUpRequest;
import ru.dodonov.user.domain.AuthenticationService;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.domain.UserRole;
import ru.dodonov.user.domain.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    JwtTokenManager jwtTokenManager;

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthenticationService authenticationService;

    @Test
    void authUser_ValidRequest_ReturnsJwtToken() {
        String login = "user";
        String password = "password";
        User mockUser = new User(1L, login, UserRole.USER, "hash");
        SignInUpRequest request = new SignInUpRequest(login, password);
        String expectedToken = "test.token";

        when(userService.isUserExistsByLogin(login)).thenReturn(true);
        when(userService.getUserByLogin(login)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, mockUser.passwordHash())).thenReturn(true);
        when(jwtTokenManager.generateToken(mockUser)).thenReturn(expectedToken);

        String result = authenticationService.authenticateUser(request);

        assertEquals(expectedToken, result);
        verify(jwtTokenManager).generateToken(mockUser);
    }

    @Test
    void authUser_IncorrectPassword_ThrowsBadCredentialsException() {
        String login = "user";
        String password = "password";
        User mockUser = new User(1L, login, UserRole.USER, "hash");
        SignInUpRequest request = new SignInUpRequest(login, password);

        when(userService.isUserExistsByLogin(login)).thenReturn(true);
        when(userService.getUserByLogin(login)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, mockUser.passwordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticateUser(request));

        verify(passwordEncoder).matches(password, mockUser.passwordHash());
        verifyNoInteractions(jwtTokenManager);
    }

    @Test
    void authUser_UserNotExists_ThrowsBadCredentialsException() {
        SignInUpRequest request = new SignInUpRequest("user", "password");

        when(userService.isUserExistsByLogin(request.login())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticateUser(request));

        verify(userService).isUserExistsByLogin(request.login());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(passwordEncoder, jwtTokenManager);
    }
}
