package ru.dodonov.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.dodonov.security.JwtTokenManager;
import ru.dodonov.user.api.SignInUpRequest;
import ru.dodonov.user.api.UserController;
import ru.dodonov.user.api.UserDto;
import ru.dodonov.user.api.UserDtoMapper;
import ru.dodonov.user.domain.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRegistrationService regService;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private UserDtoMapper dtoMapper;

    @MockBean
    private JwtTokenManager jwtTokenManager;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser_ValidRequest_ReturnsCreated() throws Exception {
        SignInUpRequest request = new SignInUpRequest("testuser", "password");
        User registeredUser = new User(1L, "testuser", UserRole.USER, "hashedPassword");
        UserDto userDto = new UserDto(1L, "testuser", UserRole.USER);

        when(regService.registerUser(any(SignInUpRequest.class))).thenReturn(registeredUser);
        when(dtoMapper.toDto(registeredUser)).thenReturn(userDto);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    public void testRegisterUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        SignInUpRequest invalidRequest = new SignInUpRequest("testuser", "");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_ValidCredentials_ReturnsJwtToken() throws Exception {
        SignInUpRequest request = new SignInUpRequest("login", "password");
        String expectedToken = "mock.jwt.token";

        when(authService.authenticateUser(any(SignInUpRequest.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsBadRequest() throws Exception {
        SignInUpRequest request = new SignInUpRequest("login", "");
        String expectedToken = "mock.jwt.token";

        when(authService.authenticateUser(any(SignInUpRequest.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
