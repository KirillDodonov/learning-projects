package org.ru.dodonov;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Task {
    long id;
    String name;
    String description;
    TaskStatus status;
    LocalDate dueDate;
}
