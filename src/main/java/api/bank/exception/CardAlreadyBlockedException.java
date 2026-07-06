package api.bank.exception;

// Исключение: карта уже заблокирована
public class CardAlreadyBlockedException extends RuntimeException {
    public CardAlreadyBlockedException(String message) {
        super(message);
    }
}
