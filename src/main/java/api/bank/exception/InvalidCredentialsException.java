package api.bank.exception;

// Исключение: неверные учётные данные
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
