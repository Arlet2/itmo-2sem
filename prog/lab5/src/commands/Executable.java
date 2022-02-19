package commands;

import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

/**
 * interface for all commands
 * <p>Uses for execute command</p>
 */
public interface Executable {
    /**
     * method that execute when command is invoke
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     * @throws IncorrectArgumentException if requiring args is incorrect
     * @throws MissingArgumentException if requiring args is missing
     */
    void execute(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException;
}
