package ru.dodonov.user.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dodonov.user.domain.AuthenticationService;
import ru.dodonov.user.domain.UserRegistrationService;
import ru.dodonov.user.domain.User;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRegistrationService regService;
    private final AuthenticationService authService;
    private final UserDtoMapper dtoMapper;

    public UserController(
            UserRegistrationService regService,
            AuthenticationService authService,
            UserDtoMapper dtoMapper
    ) {
        this.regService = regService;
        this.authService = authService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignInUpRequest request
    ) {
        User registeredUser = regService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoMapper.toDto(registeredUser));
    }

    @PostMapping("/auth")
    public ResponseEntity<String> loginUser(
            @RequestBody @Valid SignInUpRequest request
    ) {
        String jwtToken = authService.authenticateUser(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jwtToken);
    }
}
