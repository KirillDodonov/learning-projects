package ru.dodonov.user.api;

import org.springframework.stereotype.Component;
import ru.dodonov.user.domain.User;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.role()
        );
    }
}
