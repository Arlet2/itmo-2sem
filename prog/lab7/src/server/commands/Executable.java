package server.commands;

import exceptions.IncorrectArgumentException;
import server.connection_control.User;

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
    String execute(User user, CommandController commandController, String[] args) throws IncorrectArgumentException,
            IOException, ClassNotFoundException;
}
