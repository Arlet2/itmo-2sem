package server.commands;

import exceptions.IncorrectArgumentException;
import server.connection_control.User;

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
    public String execute(User user, CommandController commandController, String[] args)
            throws IOException, IncorrectArgumentException {
        try {
            commandController.getDataController().clearMap(user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("не удалось удалить данные из базы данных");
        }
        commandController.getDataController().updateModificationTime();
        return "Коллекция успешно очищена от ваших объектов.\n";
    }
}
