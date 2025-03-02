package ru.dodonov.task.api;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import ru.dodonov.task.domain.TaskStatus;

import java.time.LocalDateTime;


public record TaskDto (
        Long id,
        Long ownerId,
        @NotBlank(message = "Name can not be blank")
        String name,
        String description,
        TaskStatus status,
        @Future(message = "Date must be in future")
        LocalDateTime dueDate
) {
}
