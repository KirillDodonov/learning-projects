package ru.dodonov.task.domain;

import java.time.LocalDateTime;

public record Task (
        Long id,
        Long ownerId,
        String name,
        String description,
        TaskStatus status,
        LocalDateTime dueDate
) {
}
