package api.bank.exception;

// Исключение: недостаточно средств
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
