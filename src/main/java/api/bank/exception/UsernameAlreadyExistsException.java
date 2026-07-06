package api.bank.exception;

// Исключение: имя пользователя уже занято
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
