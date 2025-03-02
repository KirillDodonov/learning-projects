package ru.dodonov.user.api;

import jakarta.validation.constraints.NotBlank;

public record SignInUpRequest(
        @NotBlank String login,
        @NotBlank String password
) {
}
