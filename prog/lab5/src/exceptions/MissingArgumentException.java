package exceptions;

public class MissingArgumentException extends Exception {
    MissingArgumentException(final String msg) {
        super(msg);
    }
}
