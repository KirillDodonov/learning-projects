package ru.dodonov.user.api;

import ru.dodonov.user.domain.UserRole;

public record UserDto(
        Long Id,
        String login,
        UserRole role
) {
}
