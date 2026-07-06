package api.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("error", "Ошибка валидации");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCardNotFound(CardNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Карта не найдена", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Пользователь не найден", ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Пользователь уже существует", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Неверные учётные данные", ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Недостаточно средств", ex.getMessage());
    }

    @ExceptionHandler(SameCardTransferException.class)
    public ResponseEntity<Map<String, Object>> handleSameCard(SameCardTransferException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Нельзя перевести на ту же карту", ex.getMessage());
    }

    @ExceptionHandler(CardAlreadyBlockedException.class)
    public ResponseEntity<Map<String, Object>> handleCardAlreadyBlocked(CardAlreadyBlockedException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Карта уже заблокирована", ex.getMessage());
    }

    @ExceptionHandler(CardNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handleCardNotActive(CardNotActiveException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Карта не активна", ex.getMessage());
    }

    @ExceptionHandler(CardExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleCardExpired(CardExpiredException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Срок действия карты истёк", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Доступ запрещён", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("details", details);
        return ResponseEntity.status(status).body(body);
    }
}
