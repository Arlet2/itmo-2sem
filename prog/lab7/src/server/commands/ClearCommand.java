package server.commands;

import exceptions.IncorrectArgumentException;

import java.io.IOException;
import java.sql.SQLException;

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
    public String execute(CommandController commandController, String[] args)
            throws IOException, IncorrectArgumentException {
        try {
            commandController.getDataController().clearMap(args[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("не удалось удалить данные из базы данных");
        }
        commandController.getDataController().updateModificationTime();
        return "Коллекция успешно очищена от ваших объектов.\n";
    }
}
