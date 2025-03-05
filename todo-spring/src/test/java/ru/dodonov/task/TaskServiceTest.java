package ru.dodonov.task;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.dodonov.task.api.TaskDto;
import ru.dodonov.task.db.TaskEntity;
import ru.dodonov.task.db.TaskEntityMapper;
import ru.dodonov.task.db.TaskRepository;
import ru.dodonov.task.domain.*;
import ru.dodonov.user.domain.AuthenticationService;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.domain.UserRole;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEntityMapper entityMapper;

    @Mock
    private AuthenticationService authService;

    @InjectMocks
    private TaskService taskService;

    private final LocalDateTime testDueDate = LocalDateTime.now().plusDays(7);
    private final Long id = 1L;
    private final User testUser = new User(1L, "login", UserRole.USER, "password");

    @Test
    void findTaskById_ValidRequest_ReturnsTask() {
        TaskEntity expectedTask = new TaskEntity();
        expectedTask.setId(id);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(expectedTask));

        TaskEntity result = taskService.findTaskById(id);

        assertEquals(result, expectedTask);
    }

    @Test
    void findTaskById_TaskNotFound_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> taskService.findTaskById(id)
        );
    }

    @Test
    void createTask_ValidRequest() {
        Task taskToSave = new Task(
                null,
                testUser.id(),
                "task",
                "description",
                TaskStatus.TODO,
                testDueDate
        );

        TaskEntity savedTask = new TaskEntity(
                id,
                testUser.id(),
                "task",
                "description",
                TaskStatus.TODO,
                testDueDate
        );

        Task taskToReturn = new Task(
                id,
                testUser.id(),
                "task",
                "description",
                TaskStatus.TODO,
                testDueDate
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(savedTask);
        when(entityMapper.toDomain(savedTask)).thenReturn(taskToReturn);

        Task result = taskService.createTask(taskToSave);

        assertEquals(result, taskToReturn);
    }

    @Test
    void updateTask_ValidRequest_ChangesAllFields() {
        TaskDto taskToUpdate = new TaskDto(
                null,
                testUser.id(),
                "newName",
                "newDescription",
                TaskStatus.COMPLETED,
                testDueDate
        );

        TaskEntity entityToUpdate = new TaskEntity(
                id,
                testUser.id(),
                "oldName",
                "oldDescription",
                TaskStatus.TODO,
                testDueDate.minusDays(1)
        );

        TaskEntity updatedEntity = new TaskEntity(
                id,
                testUser.id(),
                "newName",
                "newDescription",
                TaskStatus.COMPLETED,
                testDueDate
        );

        Task updatedTask = new Task(
                id,
                testUser.id(),
                "newName",
                "newDescription",
                TaskStatus.COMPLETED,
                testDueDate
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(entityToUpdate));
        when(taskRepository.save(entityToUpdate)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedTask);

        Task result = taskService.updateTask(id, taskToUpdate);

        assertEquals(result, updatedTask);
    }

    @Test
    void updateTask_ValidRequest_ChangesOnlyNonNullFields() {
        TaskDto taskToUpdate = new TaskDto(
                null,
                testUser.id(),
                null,
                null,
                TaskStatus.COMPLETED,
                null
        );

        TaskEntity entityToUpdate = new TaskEntity(
                id,
                testUser.id(),
                "oldName",
                "oldDescription",
                TaskStatus.TODO,
                testDueDate
        );

        TaskEntity updatedEntity = new TaskEntity(
                id,
                testUser.id(),
                "oldName",
                "oldDescription",
                TaskStatus.COMPLETED,
                testDueDate
        );

        Task updatedTask = new Task(
                id,
                testUser.id(),
                "oldName",
                "oldDescription",
                TaskStatus.COMPLETED,
                testDueDate
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(entityToUpdate));
        when(taskRepository.save(entityToUpdate)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedTask);

        Task result = taskService.updateTask(id, taskToUpdate);

        assertThat(result).isEqualTo(updatedTask);
    }

    @Test
    void updateTask_TaskNotFound_ThrowsEntityNotFoundException() {
        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> taskService.updateTask(id, new TaskDto(null, null, null, null, null, null))
        );
    }

    @Test
    void updateTask_UserNotOwner_ThrowsIllegalArgumentException() {
        TaskEntity taskToUpdate = new TaskEntity();
        taskToUpdate.setId(id);
        taskToUpdate.setOwnerId(testUser.id() + 1);

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskToUpdate));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.updateTask(id, new TaskDto(null, null, null, null, null, null))
        );
    }

    @Test
    void deleteTask_ValidRequest() {
        TaskEntity taskToDelete = new TaskEntity();
        taskToDelete.setId(id);
        taskToDelete.setOwnerId(testUser.id());

        Task deletedTask = new Task(id, testUser.id(), null, null, null, null);

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskToDelete));
        when(entityMapper.toDomain(taskToDelete)).thenReturn(deletedTask);

        Task result = taskService.deleteTask(id);

        verify(taskRepository).delete(taskToDelete);
    }

    @Test
    void getUserTasks_returnsAllUserTasks() {
        List<TaskEntity> entities = Arrays.asList(
                new TaskEntity(id, testUser.id(), null, null, null, null),
                new TaskEntity(id + 1, testUser.id(), null, null, null, null)
        );

        List<Task> tasks = Arrays.asList(
                new Task(id, testUser.id(), null, null, null, null),
                new Task(id + 1, testUser.id(), null, null, null, null)
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findAllByOwnerId(testUser.id())).thenReturn(entities);
        for (int i = 0; i < entities.size(); i++) {
            when(entityMapper.toDomain(entities.get(i)))
                    .thenReturn(tasks.get(i));
        }

        List<Task> result = taskService.getUserTasks();

        assertThat(result).hasSize(2).containsExactlyElementsOf(tasks);
        verify(entityMapper, times(2)).toDomain(any(TaskEntity.class));
    }

    @Test
    void getUserTasks_ReturnsEmptyListWhenNoTasksFound() {
        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findAllByOwnerId(testUser.id())).thenReturn(List.of());

        List<Task> result = taskService.getUserTasks();

        assertThat(result).isEmpty();
        verify(entityMapper, never()).toDomain(any());
    }

    @Test
    void getUserTasksByStatus_ValidRequest() {
        TaskStatus status = TaskStatus.COMPLETED;
        List<TaskEntity> entities = Arrays.asList(
                new TaskEntity(id, testUser.id(), null, null, status, null),
                new TaskEntity(id + 1, testUser.id(), null, null, status, null)
        );

        List<Task> tasks = Arrays.asList(
                new Task(id, testUser.id(), null, null, status, null),
                new Task(id + 1, testUser.id(), null, null, status, null)
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findAllByOwnerIdAndStatus(testUser.id(), status)).thenReturn(entities);
        for (int i = 0; i < entities.size(); i++) {
            when(entityMapper.toDomain(entities.get(i)))
                    .thenReturn(tasks.get(i));
        }

        List<Task> result = taskService.getUserTasksByStatus(status);

        assertThat(result).hasSize(2).containsExactlyElementsOf(tasks);
        verify(entityMapper, times(2)).toDomain(any(TaskEntity.class));
    }

    @Test
    void getUserTasksSorted_ByStatusInAscendingOrder() {
        List<TaskEntity> entities = Arrays.asList(
                new TaskEntity(id, testUser.id(), null, null, TaskStatus.COMPLETED, null),
                new TaskEntity(id + 1, testUser.id(), null, null, TaskStatus.TODO, null),
                new TaskEntity(id + 2, testUser.id(), null, null, TaskStatus.IN_PROGRESS, null),
                new TaskEntity(id + 3, testUser.id(), null, null, TaskStatus.COMPLETED, null)
        );

        List<Task> tasks = Arrays.asList(
                new Task(id, testUser.id(), null, null, TaskStatus.TODO, null),
                new Task(id + 1, testUser.id(), null, null, TaskStatus.IN_PROGRESS, null),
                new Task(id + 2, testUser.id(), null, null, TaskStatus.COMPLETED, null),
                new Task(id + 3, testUser.id(), null, null, TaskStatus.COMPLETED, null)
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findAllByOwnerId(testUser.id())).thenReturn(entities);
        for (int i = 0; i < entities.size(); i++) {
            when(entityMapper.toDomain(entities.get(i)))
                    .thenReturn(tasks.get(i));
        }

        List<Task> result = taskService.getUserTasksSorted(TaskSortCriteria.STATUS, TaskSortOrder.ASC);

        assertThat(result).hasSize(4).containsExactlyElementsOf(tasks);
        assertThat(result.getFirst()).isEqualTo(tasks.getFirst());
        assertThat(result.getLast()).isEqualTo(tasks.getLast());
        verify(entityMapper, times(4)).toDomain(any(TaskEntity.class));
    }

    @Test
    void getUserTasksSorted_ByDateInDescendingOrder() {
        List<TaskEntity> entities = Arrays.asList(
                new TaskEntity(id, testUser.id(), null, null, null, testDueDate.plusYears(3)),
                new TaskEntity(id + 1, testUser.id(), null, null, null, testDueDate.plusYears(4)),
                new TaskEntity(id + 2, testUser.id(), null, null, null, testDueDate.plusYears(2)),
                new TaskEntity(id + 3, testUser.id(), null, null, null, testDueDate.plusYears(1))
        );

        List<Task> tasks = Arrays.asList(
                new Task(id, testUser.id(), null, null, null, testDueDate.plusYears(4)),
                new Task(id + 1, testUser.id(), null, null, null, testDueDate.plusYears(3)),
                new Task(id + 2, testUser.id(), null, null, null, testDueDate.plusYears(2)),
                new Task(id + 3, testUser.id(), null, null, null, testDueDate.plusYears(1))
        );

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(taskRepository.findAllByOwnerId(testUser.id())).thenReturn(entities);
        for (int i = 0; i < entities.size(); i++) {
            when(entityMapper.toDomain(entities.get(i)))
                    .thenReturn(tasks.get(i));
        }

        List<Task> result = taskService.getUserTasksSorted(TaskSortCriteria.DATE, TaskSortOrder.DESC);

        assertThat(result).hasSize(4).containsExactlyElementsOf(tasks);
        assertThat(result.getFirst()).isEqualTo(tasks.getFirst());
        assertThat(result.getLast()).isEqualTo(tasks.getLast());
        verify(entityMapper, times(4)).toDomain(any(TaskEntity.class));
    }
}
