package ru.dodonov.user.domain;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.dodonov.security.JwtTokenManager;
import ru.dodonov.user.api.SignInUpRequest;


@Service
public class AuthenticationService {

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            JwtTokenManager jwtTokenManager,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticateUser(SignInUpRequest request) {
        if (!userService.isUserExistsByLogin(request.login())) {
            throw new BadCredentialsException("Bad credentials");
        }
        User user = userService.getUserByLogin(request.login());
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return jwtTokenManager.generateToken(user);
    }

    public User getCurrentAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication not present");
        }
        return (User) authentication.getPrincipal();
    }
}
