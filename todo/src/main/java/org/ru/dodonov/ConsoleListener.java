package org.ru.dodonov;

import org.ru.dodonov.task.TaskService;
import org.ru.dodonov.task.TaskStatus;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleListener {
    private Scanner scanner = new Scanner(System.in);
    private TaskService taskService = new TaskService();

    public static void main(String[] args) {
        ConsoleListener consoleListener = new ConsoleListener();
        consoleListener.listenCommands();
    }

    private void listenCommands() {
        while (true) {
            System.out.println("Enter command:");
            String input = scanner.nextLine();
            Command command = Command.fromString(input);

            if (command == null) {
                System.out.println("Unknown command.");
                continue;
            }

            switch (command) {
                case ADD -> addTask();
                case LIST -> printAllTasks();
                case EDIT -> editTask();
                case DELETE -> deleteTask();
                case FILTER -> filter();
                case SORT -> sort();
                case EXIT -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }

    private void addTask() {
        System.out.println("Enter task name:");
        String name = scanner.nextLine();
        if (name.length() < 3) {
            System.out.println("Minimum task name length is 3.");
            return;
        }
        System.out.println("Enter task description:");
        String description = scanner.nextLine();
        System.out.println("Enter due date in format 'yyyy-mm-dd':");
        LocalDate dueDate = LocalDate.parse(scanner.nextLine());
        if (!dueDate.isAfter(LocalDate.now())) {
            System.out.println("Date must be after now.");
            return;
        }
        taskService.addTask(name, description, dueDate);
        System.out.println("Task added.");
    }

    private void printAllTasks() {
        taskService.getAllTasks().forEach(System.out::println);
    }

    private void editTask() {
        System.out.println("Enter task id:");
        int id = scanner.nextInt();
        
        Optional<String> newName = askForString("Enter new name. Or leave the line blank:");
        Optional<String> newDescription = askForString("Enter new description. Or leave the line blank:");
        Optional<TaskStatus> newStatus = askForTaskStatus();
        Optional<LocalDate> newDueDate = askForDate();

        if (newDueDate.get().isAfter(LocalDate.now())) {
            System.out.println("Date must be after now.");
            return;
        }

        taskService.editTask(id, newName, newDescription, newStatus, newDueDate);
    }

    private Optional<String> askForString(String text) {
        System.out.println(text);
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(input);
    }

    private Optional<TaskStatus> askForTaskStatus() {
        System.out.println("Enter new status of 'TODO, IN_PROGRESS, COMPLETED'. Or leave the line blank:");
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TaskStatus.valueOf(input));
    }

    private Optional<LocalDate> askForDate() {
        System.out.println("Enter new due date in format 'yyyy-mm-dd'. Or leave the line blank:");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            return Optional.empty();
        }

        LocalDate date = LocalDate.parse(input);
        return Optional.of(date);
    }

    private void deleteTask() {
        System.out.println("Enter task id:");
        int id = Integer.parseInt(scanner.nextLine());
        taskService.deleteTaskById(id);
    }

    private void filter() {
        System.out.println("Enter the task status for the filter of 'TODO, IN_PROGRESS, COMPLETED':");
        TaskStatus status = TaskStatus.valueOf(scanner.nextLine());
        taskService.getTasksByStatus(status).forEach(System.out::println);
    }

    private void sort() {
        System.out.println("Enter 'status' if you want to sort by status or enter 'date' if you want to sort by date:");
        TaskSortCriteria sortCriteria = TaskSortCriteria.valueOf(scanner.nextLine());
        System.out.println("Sort in 'ascending' or 'descending' order:");
        SortOrder sortOrder = SortOrder.valueOf(scanner.nextLine().toUpperCase());
        taskService.sortTasks(sortCriteria, sortOrder);
    }
}

