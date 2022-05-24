package client.commands;

import exceptions.IncorrectArgumentException;
import server.commands.Command;
import server.commands.ProgramController;
import server.connection_control.User;

import java.io.IOException;

public class HistoryCommand extends Command {
    HistoryCommand() {
        super("history", "", "выводит последние " + CommandController.MAX_COMMANDS_IN_HISTORY + " команд",
                CommandType.INFO);
    }

    @Override
    public String execute(User user, ProgramController programController, String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        return null;
    }
}
