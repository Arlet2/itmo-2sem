package exceptions;

public class SoManyArgumentsException extends Exception {
    public SoManyArgumentsException(final String msg) {
        super(msg);
    }
}
