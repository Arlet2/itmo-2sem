package exceptions;

/**
 * Throws when command name is not on a list of commands
 */
public class UnknownCommandException extends Exception {
    public UnknownCommandException() {
        super();
    }
}
