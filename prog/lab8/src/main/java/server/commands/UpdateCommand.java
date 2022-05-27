package server.commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateCommand extends Command {
    public UpdateCommand() {
        super("update",
                Command.SendInfo.CITY_UPDATE,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.ID}, CommandType.CHANGE);
    }

    /**
     * update element with id from args by new from console input
     *
     * @param programController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Deprecated
    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        City city = (City) args;
        long id = city.getId();
        if (programController.getDataController().isUniqueId(id))
            throw new IncorrectArgumentException("id_not_exist");
        try {
            if (!programController.getDataController().getDataBaseController().isOwner(user.getLogin(), id))
                throw new IncorrectArgumentException("not_owner");
            deleteNullValues(programController.getDataController().getMap().get(id), city);
            city.setOwner(user.getLogin());
            programController.getDataController().updateCity(city);
            return "collection_modified";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("update_failed");
        }
    }
}
