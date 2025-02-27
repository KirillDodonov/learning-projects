package ru.dodonov.user;

public record User(
        Long Id,
        String username,
        String passwordHash
) {
}
