package ru.dodonov.user;

import jakarta.validation.constraints.NotBlank;

public record SignInUpRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
