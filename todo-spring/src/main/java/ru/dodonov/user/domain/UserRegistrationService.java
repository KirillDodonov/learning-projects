package ru.dodonov.user.domain;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.dodonov.user.api.SignInUpRequest;
import ru.dodonov.user.db.UserEntity;
import ru.dodonov.user.db.UserEntityMapper;
import ru.dodonov.user.db.UserRepository;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper entityMapper;

    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserEntityMapper entityMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityMapper = entityMapper;
    }

    public User registerUser(SignInUpRequest request) {
        UserEntity userToSave = new UserEntity(
                null,
                request.login(),
                UserRole.USER,
                passwordEncoder.encode(request.password())
        );
        UserEntity savedUser = userRepository.save(userToSave);

        return entityMapper.toDomain(savedUser);
    }
}
