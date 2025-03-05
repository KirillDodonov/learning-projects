package ru.dodonov.task.domain;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.dodonov.task.api.TaskDto;
import ru.dodonov.task.db.TaskEntity;
import ru.dodonov.task.db.TaskEntityMapper;
import ru.dodonov.task.db.TaskRepository;
import ru.dodonov.user.domain.AuthenticationService;
import ru.dodonov.user.domain.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskEntityMapper entityMapper;
    private final AuthenticationService authService;

    public TaskService(
            TaskRepository taskRepository,
            TaskEntityMapper entityMapper,
            AuthenticationService authService
    ) {
        this.taskRepository = taskRepository;
        this.entityMapper = entityMapper;
        this.authService = authService;
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
        User user = authService.getCurrentAuthenticatedUser();

        TaskEntity entity = new TaskEntity(
                null,
                user.id(),
                taskToCreate.name(),
                taskToCreate.description(),
                TaskStatus.TODO,
                taskToCreate.dueDate()
        );
        TaskEntity createdTask = taskRepository.save(entity);

        return entityMapper.toDomain(createdTask);
    }

    public Task updateTask(Long taskId,TaskDto task) {
        User user = authService.getCurrentAuthenticatedUser();
        TaskEntity taskToUpdate = findTaskById(taskId);

        if (taskToUpdate.getOwnerId() != user.id()) {
            throw new IllegalArgumentException("Can not update task with provided id. Owner id must be equal");
        }

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

    public Task deleteTask(Long taskId) {
        User user = authService.getCurrentAuthenticatedUser();
        TaskEntity taskToDelete = findTaskById(taskId);

        if (taskToDelete.getOwnerId() != user.id()) {
            throw new IllegalArgumentException("Can not update task with provided id. Owner id must be equal");
        }

        taskRepository.delete(taskToDelete);
        return entityMapper.toDomain(taskToDelete);
    }

    public List<Task> getUserTasks() {
        User user = authService.getCurrentAuthenticatedUser();
        return taskRepository.findAllByOwnerId(user.id())
                .stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Task> getUserTasksByStatus(TaskStatus status) {
        User user = authService.getCurrentAuthenticatedUser();
        return taskRepository.findAllByOwnerIdAndStatus(user.id(), status)
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Task> getUserTasksSorted(
            TaskSortCriteria criteria,
            TaskSortOrder order
    ) {
        Comparator<Task> comparator;

        if (criteria == TaskSortCriteria.DATE) {
            comparator = Comparator.comparing(Task::dueDate);
        } else {
            comparator = Comparator.comparing(Task::status);
        }

        if (order == TaskSortOrder.DESC) {
            comparator = comparator.reversed();
        }
        List<Task> tasks = getUserTasks();
        tasks.sort(comparator);
        return tasks;
    }
}
