package ru.dodonov.user.db;

import org.springframework.stereotype.Component;
import ru.dodonov.user.domain.User;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getRole(),
                userEntity.getPasswordHash()
        );
    }
}
