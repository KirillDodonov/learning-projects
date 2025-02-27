package ru.dodonov.user;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper entityMapper;

    public UserService(
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
                request.username(),
                passwordEncoder.encode(request.password())
        );
        UserEntity savedUser = userRepository.save(userToSave);

        return entityMapper.toDomain(savedUser);
    }
}
