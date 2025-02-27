package ru.dodonov.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    private final UserRepository userRepository;
    private final UserEntityMapper entityMapper;

    public CurrentUserProvider(
            UserRepository userRepository,
            UserEntityMapper entityMapper
    ) {
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username %s not found"
                        .formatted(username)));

        return entityMapper.toDomain(user);
    }
}
