package ru.dodonov.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.dodonov.user.db.UserEntity;
import ru.dodonov.user.db.UserEntityMapper;
import ru.dodonov.user.db.UserRepository;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.domain.UserRole;
import ru.dodonov.user.domain.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEntityMapper entityMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByLogin_ValidLogin_ReturnsUser() {
        String login = "testLogin";
        User expectedUser = new User(
                1L,
                login,
                UserRole.USER,
                "password"
        );
        UserEntity expectedEntity = new UserEntity(
                1L,
                login,
                UserRole.USER,
                "password"
        );

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(expectedEntity));
        when(entityMapper.toDomain(expectedEntity)).thenReturn(expectedUser);

        User result = userService.getUserByLogin(login);

        assertNotNull(result);
        assertEquals(result, expectedUser);
    }

    @Test
    void getUserByLogin_UserNotFound_ThrowsEntityNotFoundException() {
        String login = "incorrectLogin";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserByLogin(login)
        );

        assertEquals("User with login " + login + " not found", exception.getMessage());
        verifyNoInteractions(entityMapper);
    }

    @Test
    void isUserExistsByLogin_ValidLogin_ReturnsTrue() {
        String login = "testLogin";

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mock(UserEntity.class)));

        boolean result = userService.isUserExistsByLogin(login);

        assertTrue(result);
    }

    @Test
    void isUserExistsByLogin_UserNotExists_ReturnsFalse() {
        String login = "incorrectLogin";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        boolean result = userService.isUserExistsByLogin(login);

        assertFalse(result);
    }

    @Test
    void isUserExistsByLogin_WhenLoginIsNull_ReturnsFalse() {
        when(userRepository.findByLogin(null)).thenReturn(Optional.empty());

        boolean result = userService.isUserExistsByLogin(null);

        assertFalse(result);
        verify(userRepository).findByLogin(null);
    }

}
