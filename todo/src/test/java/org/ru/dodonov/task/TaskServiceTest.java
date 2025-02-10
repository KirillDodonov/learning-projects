package org.ru.dodonov.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ru.dodonov.SortOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        taskService = new TaskService();
    }

    @Test
    public void addTask() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(1000));

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals(0, tasks.get(0).getId());
        assertEquals(1, tasks.get(1).getId());
    }

    @Test
    public void getAllTasks() {
        addTask(); //Третий и второй пункт из вопроса в тг
        assertEquals(2, taskService.getAllTasks().size());
    }

    @Test
    public void editTasks() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(100));

        taskService.editTask(0,
                Optional.of("Updated 1"),
                Optional.of("Updated 1"),
                Optional.of(TaskStatus.COMPLETED),
                Optional.of(LocalDate.now().plusDays(10000))
        );

        taskService.editTask(1,
                Optional.of("Updated 2"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        List<Task> tasks = taskService.getAllTasks();
        Task firstTask = tasks.get(0);
        Task secondTask = tasks.get(1);

        Assertions.assertEquals("Updated 1", firstTask.getName());
        Assertions.assertEquals("Updated 1", firstTask.getDescription());
        Assertions.assertEquals(TaskStatus.COMPLETED, firstTask.getStatus());
        Assertions.assertEquals(LocalDate.now().plusDays(10000), firstTask.getDueDate());

        Assertions.assertEquals("Updated 2", secondTask.getName());
        Assertions.assertEquals("Description 2", secondTask.getDescription());
        Assertions.assertEquals(TaskStatus.TODO, secondTask.getStatus());
        Assertions.assertEquals(LocalDate.now().plusDays(100), secondTask.getDueDate());
    }

    @Test
    public void deleteTaskById() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(1000));

        taskService.deleteTaskById(0);
        assertEquals(1, taskService.getAllTasks().size());
        assertEquals("Task 2", taskService.getAllTasks().get(0).getName());
    }

    @Test
    public void getTasksByStatus() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(100));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(100));
        taskService.addTask("Task 4", "Description 4", LocalDate.now().plusDays(100));

        taskService.editTask(1,
                Optional.of("Updated 1"),
                Optional.empty(),
                Optional.of(TaskStatus.IN_PROGRESS),
                Optional.empty()
        );

        taskService.editTask(2,
                Optional.of("Updated 1"),
                Optional.empty(),
                Optional.of(TaskStatus.COMPLETED),
                Optional.empty()
        );

        taskService.editTask(3,
                Optional.of("Updated 1"),
                Optional.empty(),
                Optional.of(TaskStatus.IN_PROGRESS),
                Optional.empty()
        );

        Assertions.assertEquals(2, taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).size());
        Assertions.assertEquals(3, taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).get(1).getId());
    }

    @Test
    public void sortTasksByStatusInAscendingOrder() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(10));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(10));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(10));

        taskService.editTask(0,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.IN_PROGRESS),
                Optional.empty()
        );

        taskService.editTask(1,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.COMPLETED),
                Optional.empty()
        );

        taskService.editTask(2,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.TODO),
                Optional.empty()
        );

        taskService.sortTasksByStatus(SortOrder.ASCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tasks.get(1).getStatus());
        Assertions.assertEquals(TaskStatus.COMPLETED, tasks.get(2).getStatus());
    }

    @Test
    public void sortTasksByStatusInDescendingOrder() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(10));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(10));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(10));

        taskService.editTask(0,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.IN_PROGRESS),
                Optional.empty()
        );

        taskService.editTask(1,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.COMPLETED),
                Optional.empty()
        );

        taskService.editTask(2,
                Optional.empty(),
                Optional.empty(),
                Optional.of(TaskStatus.TODO),
                Optional.empty()
        );

        taskService.sortTasksByStatus(SortOrder.DESCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(TaskStatus.COMPLETED, tasks.get(0).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tasks.get(1).getStatus());
        Assertions.assertEquals(TaskStatus.TODO, tasks.get(2).getStatus());
    }

    @Test
    public void sortTasksByDateInAscendingOrder() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(10000));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(10));
        taskService.addTask("Task 4", "Description 4", LocalDate.now().plusDays(1000));
        taskService.sortTasksByDate(SortOrder.ASCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(LocalDate.now().plusDays(10), tasks.get(0).getDueDate());
        Assertions.assertEquals(LocalDate.now().plusDays(10000), tasks.get(3).getDueDate());
    }

    @Test
    public void sortTasksByDateInDescendingOrder() {
        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(10000));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(10));
        taskService.addTask("Task 4", "Description 4", LocalDate.now().plusDays(1000));
        taskService.sortTasksByDate(SortOrder.DESCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(LocalDate.now().plusDays(10000), tasks.get(0).getDueDate());
        Assertions.assertEquals(LocalDate.now().plusDays(10), tasks.get(3).getDueDate());
    }
}
