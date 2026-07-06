package api.bank.service;

import api.bank.dto.AuthResponse;
import api.bank.dto.LoginRequest;
import api.bank.dto.RegisterRequest;
import api.bank.entity.Role;
import api.bank.entity.User;
import api.bank.exception.InvalidCredentialsException;
import api.bank.exception.UsernameAlreadyExistsException;
import api.bank.repository.UserRepository;
import api.bank.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("ivan", "password123");
        when(userRepository.existsByUsername("ivan")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtUtil.generateToken("ivan")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername() {
        RegisterRequest request = new RegisterRequest("ivan", "password123");
        when(userRepository.existsByUsername("ivan")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("ivan", "password123");
        User user = User.builder()
                .id(1L)
                .username("ivan")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("ivan")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_invalidCredentials() {
        LoginRequest request = new LoginRequest("ivan", "wrongpassword");
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
