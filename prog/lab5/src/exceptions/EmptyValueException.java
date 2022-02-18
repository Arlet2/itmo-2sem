package exceptions;

public class EmptyValueException extends RuntimeException {
    public EmptyValueException(final String msg) {
        super(msg);
    }
}
