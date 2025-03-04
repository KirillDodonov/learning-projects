package ru.dodonov.user.api;

import ru.dodonov.user.domain.UserRole;

public record UserDto(
        Long id,
        String login,
        UserRole role
) {
}
