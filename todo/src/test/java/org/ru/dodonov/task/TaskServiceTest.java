package org.ru.dodonov.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ru.dodonov.SortOrder;
import org.ru.dodonov.TaskSortCriteria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        taskService = new TaskService();

        taskService.addTask("Task 1", "Description 1", LocalDate.now().plusDays(100));
        taskService.addTask("Task 2", "Description 2", LocalDate.now().plusDays(10000));
        taskService.addTask("Task 3", "Description 3", LocalDate.now().plusDays(10));
        taskService.addTask("Task 4", "Description 4", LocalDate.now().plusDays(1000));
    }

    @Test
    public void addTask() {
        taskService.addTask("Task 5", "Description 5", LocalDate.now().plusDays(100));

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(5, tasks.size());
        assertEquals(4, tasks.get(4).getId());
    }

    @Test
    public void editTasks() {
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

        Task expectedFirstTask = new Task(
                0,
                "Updated 1",
                "Updated 1",
                TaskStatus.COMPLETED,
                LocalDate.now().plusDays(10000)
        );
        Task expectedSecondTask = new Task(
                1,
                "Updated 2",
                "Description 2",
                TaskStatus.TODO,
                LocalDate.now().plusDays(10000)
        );

        Assertions.assertEquals(expectedFirstTask, firstTask);
        Assertions.assertEquals(expectedSecondTask, secondTask);
    }

    @Test
    public void deleteTaskById() {
        taskService.deleteTaskById(0);

        assertEquals(3, taskService.getAllTasks().size());
        assertEquals("Task 2", taskService.getAllTasks().get(0).getName());
    }

    @Test
    public void getTasksByStatus() {
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

        taskService.sortTasks(TaskSortCriteria.STATUS, SortOrder.ASCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(TaskStatus.TODO, tasks.getFirst().getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tasks.get(2).getStatus());
        Assertions.assertEquals(TaskStatus.COMPLETED, tasks.getLast().getStatus());
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

        taskService.sortTasks(TaskSortCriteria.STATUS, SortOrder.DESCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(TaskStatus.COMPLETED, tasks.get(0).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tasks.get(1).getStatus());
        Assertions.assertEquals(TaskStatus.TODO, tasks.get(2).getStatus());
    }

    @Test
    public void sortTasksByDateInAscendingOrder() {
        taskService.sortTasks(TaskSortCriteria.DATE, SortOrder.ASCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(LocalDate.now().plusDays(10), tasks.getFirst().getDueDate());
        Assertions.assertEquals(LocalDate.now().plusDays(10000), tasks.getLast().getDueDate());
    }

    @Test
    public void sortTasksByDateInDescendingOrder() {
        taskService.sortTasks(TaskSortCriteria.DATE, SortOrder.DESCENDING);

        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(LocalDate.now().plusDays(10000), tasks.getFirst().getDueDate());
        Assertions.assertEquals(LocalDate.now().plusDays(10), tasks.getLast().getDueDate());
    }
}
