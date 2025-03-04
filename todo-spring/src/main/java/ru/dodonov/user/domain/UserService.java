package ru.dodonov.user.domain;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.dodonov.user.db.UserEntity;
import ru.dodonov.user.db.UserEntityMapper;
import ru.dodonov.user.db.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEntityMapper entityMapper;

    public UserService(
            UserRepository userRepository,
            UserEntityMapper entityMapper
    ) {
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }

    public User getUserByLogin(String login) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User with login %s not found"
                        .formatted(login)));
        return entityMapper.toDomain(user);
    }

    public boolean isUserExistsByLogin(String login) {
        return userRepository.findByLogin(login).isPresent();
    }
}
