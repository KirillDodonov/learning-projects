package org.ru.dodonov.task;

import org.ru.dodonov.SortOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final List<Task> tasks;
    private int idCounter;

    public TaskService() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(
            String name,
            String description,
            LocalDate dueDate
    ) {
        Task task = new Task(
                idCounter,
                name,
                description,
                TaskStatus.TODO,
                dueDate);
        idCounter++;
        tasks.add(task);
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Optional<Task> getTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    public void editTask(
            int id,
            Optional<String> name,
            Optional<String> description,
            Optional<TaskStatus> status,
            Optional<LocalDate> dueDate
    ) {
        Optional<Task> optionalTask = getTaskById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            name.ifPresent(task::setName);
            description.ifPresent(task::setDescription);
            status.ifPresent(task::setStatus);
            dueDate.ifPresent(task::setDueDate);
        } else {
            System.out.println("Task with id " + id + " not found");
        }
    }

    public void deleteTaskById(int id) {
        Optional<Task> task = getTaskById(id);
        if (task.isPresent()) {
            tasks.remove(task.get());
        } else {
            System.out.println("Task with id " + id + " not found");
        }
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .toList();
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found.");
        }
        return filteredTasks;
    }

    public void sortTasksByStatus(SortOrder order) {
        Comparator<Task> comparator = Comparator.comparing(Task::getStatus);
        if (order.equals(SortOrder.DESCENDING)) comparator = comparator.reversed();
        tasks.sort(comparator);
    }

    public void sortTasksByDate(SortOrder order) {
        Comparator<Task> comparator = Comparator.comparing(Task::getDueDate);
        if (order.equals(SortOrder.DESCENDING)) comparator = comparator.reversed();
        tasks.sort(comparator);
    }
}
