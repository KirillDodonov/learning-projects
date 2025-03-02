package ru.dodonov.task.db;

import jakarta.persistence.*;
import lombok.*;
import ru.dodonov.task.domain.TaskStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tasks")

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "owner_id")
    Long ownerId;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TaskStatus status;

    @Column(name = "due_date")
    LocalDateTime dueDate;
}
