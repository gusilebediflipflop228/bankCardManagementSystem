package api.bank.service;

import api.bank.dto.PageResponse;
import api.bank.dto.UserCreateRequest;
import api.bank.dto.UserResponse;
import api.bank.entity.Role;
import api.bank.entity.User;
import api.bank.exception.UserNotFoundException;
import api.bank.exception.UsernameAlreadyExistsException;
import api.bank.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_success() {
        var page = new org.springframework.data.domain.PageImpl<>(
                java.util.List.of(
                        User.builder().id(1L).username("ivan").password("x").role(Role.USER).build()
                )
        );
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        PageResponse<UserResponse> response = userService.getAllUsers(
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, response.getContent().size());
        assertEquals("ivan", response.getContent().get(0).getUsername());
    }

    @Test
    void getUserById_success() {
        User user = User.builder().id(1L).username("ivan").password("x").role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals("ivan", response.getUsername());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void createUser_success() {
        UserCreateRequest request = new UserCreateRequest("ivan", "password123", Role.USER);
        when(userRepository.existsByUsername("ivan")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertEquals("ivan", response.getUsername());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void createUser_duplicateUsername() {
        UserCreateRequest request = new UserCreateRequest("ivan", "password123", Role.USER);
        when(userRepository.existsByUsername("ivan")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
    }
}
