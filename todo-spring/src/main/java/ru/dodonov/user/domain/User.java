package ru.dodonov.user.domain;


public record User(
        Long Id,
        String login,
        UserRole role,
        String passwordHash
) {
}
