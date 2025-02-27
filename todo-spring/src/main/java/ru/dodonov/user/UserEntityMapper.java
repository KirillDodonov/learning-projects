package ru.dodonov.user;

import org.springframework.stereotype.Component;
import ru.dodonov.task.db.TaskEntity;
import ru.dodonov.task.domain.Task;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPasswordHash()
        );
    }
}
