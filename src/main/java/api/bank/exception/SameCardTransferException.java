package api.bank.exception;

// Исключение: перевод на ту же карту
public class SameCardTransferException extends RuntimeException {
    public SameCardTransferException(String message) {
        super(message);
    }
}
