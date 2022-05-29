package server.commands;

import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;

import java.sql.SQLException;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear", CommandType.CHANGE);
    }

    /**
     * Delete all cities that user is owned
     *
     * @param user              that execute this command
     * @param programController that execute this command
     * @param args              of command
     * @return reply
     * @throws IncorrectArgumentException if database return error
     */
    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException {
        try {
            programController.getDataController().clearMap(user.getLogin());
        } catch (SQLException e) {
            throw new IncorrectArgumentException("clearing_data_failed");
        }
        return "clear_success";
    }
}
