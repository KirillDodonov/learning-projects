package ru.dodonov.task.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dodonov.task.domain.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskDtoMapper dtoMapper;

    public TaskController(
            TaskService taskService,
            TaskDtoMapper dtoMapper
    ) {
        this.taskService = taskService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody @Valid TaskDto task
    ) {
        Task createdTask = taskService.createTask(dtoMapper.toDomain(task));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoMapper.toDto(createdTask));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskDto task
    ) {
        Task updatedTask = taskService.updateTask(taskId, task);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoMapper.toDto(updatedTask));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDto> deleteTask(
            @PathVariable Long taskId
    ) {
        Task deletedTask = taskService.deleteTask(taskId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(dtoMapper.toDto(deletedTask));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getUserTasks() {
        List<Task> tasks = taskService.getUserTasks();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks.stream()
                        .map(dtoMapper::toDto)
                        .toList());
    }

    @GetMapping("/my/{status}")
    public ResponseEntity<List<TaskDto>> getUserTasksByStatus(
            @PathVariable TaskStatus status
    ) {
        List<Task> tasks = taskService.getUserTasksByStatus(status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(tasks.stream()
                        .map(dtoMapper::toDto)
                        .toList());
    }

    @GetMapping("/my/sorted")
    public ResponseEntity<List<TaskDto>> getUserTasksSorted(
            @RequestParam TaskSortCriteria criteria,
            @RequestParam TaskSortOrder order
    ) {
        List<Task> tasks = taskService.getUserTasksSorted(criteria, order);
        return ResponseEntity.status(HttpStatus.OK)
                .body(tasks.stream()
                        .map(dtoMapper::toDto)
                        .toList());
    }
}
