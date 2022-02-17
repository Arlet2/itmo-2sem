package commands;

import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public interface Executable {
    // args[0] хранит имя команды
    void execute(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException;
}
