package ru.dodonov.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(unique = true, name = "login")
    String username;

    @Column(name = "passwordHash")
    String passwordHash;
}
