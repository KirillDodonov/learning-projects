package ru.dodonov.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.dodonov.user.domain.User;
import ru.dodonov.user.domain.UserService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    public JwtTokenFilter(
            JwtTokenManager jwtTokenManager,
            UserService userService
    ) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        var jwtToken = authorization.substring(7);

        if (!jwtTokenManager.isTokenValid(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String login = jwtTokenManager.getLoginFromToken(jwtToken);
        String role = jwtTokenManager.getRoleFromToken(jwtToken);

        User user = userService.getUserByLogin(login);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }

}
