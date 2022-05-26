package server.commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import server.connection_control.User;

import java.io.IOException;
import java.sql.SQLException;

public class InsertCommand extends Command {
    InsertCommand() {
        super("insert", Command.SendInfo.CITY,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.ID}, CommandType.CHANGE);
    }

    /**
     * insert element with id in args
     * <p>Change modification time if command completes</p>
     *
     * @param programController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        City city = (City) args;
        if (!programController.getDataController().isUniqueId(city.getId()))
            throw new IncorrectArgumentException("not_unique_id");
        try {
            programController.getDataController().addCity(city, user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("not_unique_id");
        }
        programController.getDataController().updateModificationTime();
        return "insert_success";
    }
}
