package mate.academy.bookstore.exception;

public class SpecificationNotFoundException extends RuntimeException {
    public SpecificationNotFoundException(String text) {
        super(text);
    }
}
