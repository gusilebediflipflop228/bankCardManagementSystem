package api.bank.exception;

// Исключение: карта не активна
public class CardNotActiveException extends RuntimeException {
    public CardNotActiveException(String message) {
        super(message);
    }
}
