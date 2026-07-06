package api.bank.exception;

// Исключение: пользователь не найден
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
