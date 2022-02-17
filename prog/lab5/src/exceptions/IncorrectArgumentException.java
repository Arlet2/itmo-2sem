package exceptions;

public class IncorrectArgumentException extends Exception{
    IncorrectArgumentException(final String msg) {
        super(msg);
    }
}
