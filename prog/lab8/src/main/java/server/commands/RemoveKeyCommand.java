package server.commands;

import exceptions.IncorrectArgumentException;
import server.connection_control.User;

import java.sql.SQLException;

public class RemoveKeyCommand extends Command {
    RemoveKeyCommand() {
        super("remove_key", null,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.ID}, CommandType.CHANGE);
    }

    /**
     * remove element with id from args
     * <p>Change modification time if command completes</p>
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
        if (programController.getDataController().isUniqueId(id))
            throw new IncorrectArgumentException("id_not_exist");
        try {
            if (!programController.getDataController().getDataBaseController().isOwner(user.getLogin(), id))
                return "not_owner";
            programController.getDataController().removeCity(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("remove_data_failed");
        }
        programController.getDataController().updateModificationTime();
        return "remove_success";
    }
}
