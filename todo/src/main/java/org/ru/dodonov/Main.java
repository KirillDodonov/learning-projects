package org.ru.dodonov;

import org.ru.dodonov.task.TaskService;
import org.ru.dodonov.task.TaskStatus;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskService taskService = new TaskService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("Enter command:");
            String command = scanner.nextLine();
            switch (command) {
                case "add" -> handleAddCommand();
                case "list" -> handleGetAllTasksCommand();
                case "edit" -> handleEditCommand();
                case "delete" -> handleDeleteCommand();
                case "filter" -> handleFilterCommand();
                case "sort" -> handleSortCommand();
                case "exit" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Unknown command.");
            }
        }
    }

    private static void handleAddCommand() {
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

    private static void handleGetAllTasksCommand() {
        taskService.getAllTasks().forEach(System.out::println);
    }

    //не уверен в адекватности реализации
    private static void handleEditCommand() {
        System.out.println("Enter task id:");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter new title. Or leave the line blank:");
        String newName = scanner.nextLine();
        Optional<String> newNameOptional;
        if (newName.isEmpty()) {
            newNameOptional = Optional.empty();
        } else {
            newNameOptional = Optional.of(newName);
        }

        System.out.println("Enter new description. Or leave the line blank:");
        String newDescription = scanner.nextLine();
        Optional<String> newDescriptionOptional;
        if (newDescription.isEmpty()) {
            newDescriptionOptional = Optional.empty();
        } else {
            newDescriptionOptional = Optional.of(newDescription);
        }

        System.out.println("Enter new status of 'TODO, IN_PROGRESS, COMPLETED'. Or leave the line blank:");
        String newStatus = scanner.nextLine();
        Optional<TaskStatus> newStatusOptional;
        if (newStatus.isEmpty()) {
            newStatusOptional = Optional.empty();
        } else {
            newStatusOptional = Optional.of(TaskStatus.valueOf(newStatus));
        }

        System.out.println("Enter new due date in format 'yyyy-mm-dd'. Or leave the line blank:");
        String newDueDate = scanner.nextLine();
        Optional<LocalDate> newDueDateOptional;
        if (newDueDate.isEmpty()) {
            newDueDateOptional = Optional.empty();
        } else {
            newDueDateOptional = Optional.of(LocalDate.parse(newDueDate));
            if (!LocalDate.parse(newDueDate).isAfter(LocalDate.now())) {
                System.out.println("Date must be after now:");
                return;
            }
        }

        taskService.editTask(
                id,
                newNameOptional,
                newDescriptionOptional,
                newStatusOptional,
                newDueDateOptional
        );
    }

    private static void handleDeleteCommand() {
        System.out.println("Enter task id:");
        int id = Integer.parseInt(scanner.nextLine());
        taskService.deleteTaskById(id);
    }

    private static void handleFilterCommand() {
        System.out.println("Enter the task status for the filter of 'TODO, IN_PROGRESS, COMPLETED':");
        TaskStatus status = TaskStatus.valueOf(scanner.nextLine());
        taskService.getTasksByStatus(status).forEach(System.out::println);
    }

    private static void handleSortCommand() {
        System.out.println("Enter 'status' if you want to sort by status or enter 'date' if you want to sort by date:");
        String sortBy = scanner.nextLine();
        System.out.println("Sort in 'ascending' or 'descending' order:");
        SortOrder sortOrder = SortOrder.valueOf(scanner.nextLine().toUpperCase());
        if (sortBy.equals("date")) {
            taskService.sortTasksByDate(sortOrder);
        } else if (sortBy.equals("status")) {
            taskService.sortTasksByStatus(sortOrder);
        } else {
            System.out.println("Incorrect command.");
            return;
        }
    }
}

