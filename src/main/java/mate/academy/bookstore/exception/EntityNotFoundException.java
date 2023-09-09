package mate.academy.bookstore.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String text) {
        super(text);
    }
}
