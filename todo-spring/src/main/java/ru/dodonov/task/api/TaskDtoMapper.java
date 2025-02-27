package ru.dodonov.task.api;

import org.springframework.stereotype.Component;
import ru.dodonov.task.domain.Task;

@Component
public class TaskDtoMapper {

    public TaskDto toDto(Task task) {
        return new TaskDto(
                task.id(),
                task.ownerId(),
                task.name(),
                task.description(),
                task.status(),
                task.dueDate()
        );
    }

    public Task toDomain(TaskDto dto) {
        return new Task(
                dto.id(),
                dto.ownerId(),
                dto.name(),
                dto.description(),
                dto.status(),
                dto.dueDate()
        );
    }
}
