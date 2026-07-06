package api.bank.exception;

// Исключение: срок действия карты истёк
public class CardExpiredException extends RuntimeException {
    public CardExpiredException(String message) {
        super(message);
    }
}
