package commands;

import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.io.IOException;

/**
 * interface for all commands
 * <p>Uses for execute command</p>
 */
public interface Executable {
    /**
     * method that execute when command is invoke
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    String execute(CommandController commandController, String[] args) throws IncorrectArgumentException,
            IOException, ClassNotFoundException;
}
