package server.commands;

import java.io.IOException;

public class HistoryCommand extends Command {
    HistoryCommand() {
        super("history", "", "выводит последние " + CommandController.MAX_COMMANDS_IN_HISTORY + " команд",
                null, null, false);
    }

    /**
     * print history of command that was used in console
     * <p>Max commands in history can be changed in CommandController</p>
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        if (commandController.getHistory().isEmpty()) {
            return "История команд пуста.";
        }
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < commandController.getHistory().size(); i++) {
            if (i % 5 == 0 && i != 0)
                data.append("\n");
            data.append(i + 1).append(") ").append(commandController.getHistory().get(i).getName()).append(" ");
        }
        return data.toString() + '\n';
    }
}
