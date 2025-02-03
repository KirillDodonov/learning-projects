package org.ru.dodonov;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final List<Task> tasks;
    private long idCounter;

    public TaskService(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void createTask(Task task) {
        task.setId(idCounter);
        idCounter++;
        tasks.add(task);
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task getTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task with id %s not found"
                        .formatted(id)));
    }

    public void deleteTaskById(int id) {
        Task task = getTaskById(id);
        tasks.remove(task);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .toList();

        if (filteredTasks.isEmpty()) {
            throw new IllegalArgumentException("Task with status %s not found"
                    .formatted(status));
        }

        return filteredTasks;
    }


}
