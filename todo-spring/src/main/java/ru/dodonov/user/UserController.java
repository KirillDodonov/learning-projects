package ru.dodonov.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignInUpRequest request
    ) {
        User registeredUser = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

    @PostMapping("/auth")
    public ResponseEntity<UserDto> loginUser(
            @RequestBody @Valid SignInUpRequest request
    ) {
//        User registeredUser = userService.authenticateUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }
}
