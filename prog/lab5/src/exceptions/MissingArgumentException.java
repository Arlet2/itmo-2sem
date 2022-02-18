package exceptions;

public class MissingArgumentException extends Exception {
    public MissingArgumentException(final String msg) {
        super(msg);
    }
}
