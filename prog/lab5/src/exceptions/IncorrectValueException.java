package exceptions;

public class IncorrectValueException extends RuntimeException {
    public IncorrectValueException(String msg) {
        super(msg);
    }
}
