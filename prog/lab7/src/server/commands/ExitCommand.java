package server.commands;

import connect_utils.CommandInfo;
import server.Logger;

import java.io.IOException;
import java.util.logging.Level;

public class ExitCommand extends Command {
    ExitCommand() {
        super("exit", "", "завершает программу (без сохранения)", CommandInfo.SendInfo.EXIT,
                null, false);
    }

    /**
     * exit from command
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        commandController.getConnectionController().disconnect();
        Logger.getLogger().log(Level.INFO, "Пользователь отключился от сервера.");
        return null;
    }
}
