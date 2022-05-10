package server.commands;

import connect_utils.CommandInfo;
import data_classes.City;
import exceptions.IncorrectArgumentException;

import java.sql.SQLException;

public class RemoveKeyCommand extends Command {
    RemoveKeyCommand() {
        super("remove_key", "id", "удаляет элемент из коллекции с заданным ключом", null,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }

    /**
     * remove element with id from args
     * <p>Change modification time if command completes</p>
     *
     * @param commandController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException {
        long id = Long.parseLong(args[1]);
        if (City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с таким id не существует");
        try {
            if (!commandController.getDataController().getDataBaseController().isOwner(args[0], id))
                return "Вы не владеете этим объектом и не можете его удалить.\n";
            commandController.getDataController().getDataBaseController().removeCity(id);
            commandController.getDataController().getMap().remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("не удалось удалить элемент из базы данных");
        }
        commandController.getDataController().updateModificationTime();
        return "Элемент с id " + id + " был удалён.\n";
    }
}
