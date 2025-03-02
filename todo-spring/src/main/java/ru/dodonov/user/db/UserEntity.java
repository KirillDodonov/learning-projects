package ru.dodonov.user.db;

import jakarta.persistence.*;
import lombok.*;
import ru.dodonov.user.domain.UserRole;

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
    String login;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    UserRole role;

    @Column(name = "passwordHash")
    String passwordHash;
}
