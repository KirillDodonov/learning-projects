package org.ru.dodonov.task;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Task {
    int id;
    String name;
    String description;
    TaskStatus status;
    LocalDate dueDate;
}
