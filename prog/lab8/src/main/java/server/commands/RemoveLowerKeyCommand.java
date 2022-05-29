package server.commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;

import java.sql.SQLException;
import java.util.ArrayList;


public class RemoveLowerKeyCommand extends Command {
    public RemoveLowerKeyCommand() {
        super("remove_lower_key", null,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.ID}, CommandType.CHANGE);
    }

    /**
     * remove all elements with id that lower than id in args
     * <p>Modification time can be changed</p>
     *
     * @param programController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException {
        String[] strArgs = (String[]) args;
        long id = Long.parseLong(strArgs[1]);
        boolean isMapModified = false;
        ArrayList<Long> deleteId = new ArrayList<>();
        for (City city : programController.getDataController().getMap().values()) {
            System.out.println(city.getId() + " " + id + " : " + programController.getDataController().getMap().size());
            if (city.getId() < id) {
                try {
                    if (!programController.getDataController().getDataBaseController()
                            .isOwner(user.getLogin(), city.getId()))
                        continue;
                } catch (SQLException ignored) {
                }
                deleteId.add(city.getId());
            }
        }
        for (long dId : deleteId) {
            try {
                programController.getDataController().removeCity(dId);
            } catch (SQLException ignored) {

            }
            isMapModified = true;
        }
        if (isMapModified)
            return "collection_modified";
        return "collection_not_modified";
    }
}
