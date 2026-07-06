package api.bank.exception;

// Исключение: карта не найдена
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
