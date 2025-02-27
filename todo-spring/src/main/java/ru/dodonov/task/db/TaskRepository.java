package ru.dodonov.task.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dodonov.task.domain.Task;
import ru.dodonov.task.domain.TaskStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByOwnerId(Long ownerId);
    List<TaskEntity> findAllByOwnerIdAndStatus(Long id, TaskStatus status);
}
