package org.ru.dodonov.task;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
}
