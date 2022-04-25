package server.commands;

import java.io.IOException;

public class ClearCommand extends Command {
    ClearCommand() {
        super("clear", "", "очищает элементы коллекции", null, null, false);
    }

    /**
     * Clear collection
     * <p>Change modification time</p>
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        commandController.getDataController().getMap().clear();
        commandController.getDataController().updateModificationTime();
        return "Коллекция успешно очищена.\n";
    }
}
