package ru.dodonov.task.domain;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.dodonov.task.api.TaskDto;
import ru.dodonov.task.db.TaskEntity;
import ru.dodonov.task.db.TaskEntityMapper;
import ru.dodonov.task.db.TaskRepository;
import ru.dodonov.user.domain.AuthenticationService;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.db.UserRepository;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskEntityMapper entityMapper;
    private final AuthenticationService userProvider;

    public TaskService(
            TaskRepository taskRepository,
            TaskEntityMapper entityMapper, AuthenticationService userProvider,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.entityMapper = entityMapper;
        this.userProvider = userProvider;
    }

    public TaskEntity findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Event entity wasn't found by id=%s"
                        .formatted(taskId)));
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Can not create location with provided id. Id Must be empty");
        }

        User user = userProvider.getCurrentAuthenticatedUser();

        TaskEntity entity = new TaskEntity(
                null,
                user.Id(),
                taskToCreate.name(),
                taskToCreate.description(),
                TaskStatus.TODO,
                taskToCreate.dueDate()
        );
        TaskEntity createdTask = taskRepository.save(entity);

        return entityMapper.toDomain(createdTask);
    }

    public Task updateTask(Long taskId,TaskDto task) {
        TaskEntity taskToUpdate = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Event entity wasn't found by id=%s"
                        .formatted(taskId)));

        Optional.ofNullable(task.name())
                .ifPresent(taskToUpdate::setName);
        Optional.ofNullable(task.description())
                .ifPresent(taskToUpdate::setDescription);
        Optional.ofNullable(task.status())
                .ifPresent(taskToUpdate::setStatus);
        Optional.ofNullable(task.dueDate())
                .ifPresent(taskToUpdate::setDueDate);
        
        TaskEntity updatedTask = taskRepository.save(taskToUpdate);
        return entityMapper.toDomain(updatedTask);
    }

    public void deleteTask(Long taskId) {
        TaskEntity taskToDelete = findTaskById(taskId);
        taskRepository.delete(taskToDelete);
    }

    public List<Task> getUserTasks() {
        User user = userProvider.getCurrentAuthenticatedUser();
        return taskRepository.findAllByOwnerId(user.Id())
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Task> getUserTasksByStatus(TaskStatus status) {
        User user = userProvider.getCurrentAuthenticatedUser();
        return taskRepository.findAllByOwnerIdAndStatus(user.Id(), status)
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Task> getUserTasksSorted(
            TaskSortCriteria criteria,
            SortOrder order
    ) {
        Comparator<Task> comparator;

        if (criteria == TaskSortCriteria.DATE) {
            comparator = Comparator.comparing(Task::dueDate);
        } else {
            comparator = Comparator.comparing(Task::status);
        }

        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }
        List<Task> tasks = getUserTasks();
        tasks.sort(comparator);
        return tasks;
    }
}
