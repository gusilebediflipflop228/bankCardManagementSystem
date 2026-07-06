package api.bank.controller;

import api.bank.dto.*;
import api.bank.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация и вход в систему")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создаёт нового пользователя с ролью USER")
    @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован")
    @ApiResponse(responseCode = "409", description = "Пользователь уже существует")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентификация и получение JWT-токена")
    @ApiResponse(responseCode = "200", description = "Вход выполнен успешно")
    @ApiResponse(responseCode = "401", description = "Неверные учётные данные")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
