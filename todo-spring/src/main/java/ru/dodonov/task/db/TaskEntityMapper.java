package ru.dodonov.task.db;

import org.springframework.stereotype.Component;
import ru.dodonov.task.domain.Task;

@Component
public class TaskEntityMapper {
    public Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getDueDate()
        );
    }
}
