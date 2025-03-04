package ru.dodonov.user.domain;


public record User(
        Long id,
        String login,
        UserRole role,
        String passwordHash
) {
}
