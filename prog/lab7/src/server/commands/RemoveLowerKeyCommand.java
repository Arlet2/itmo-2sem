package server.commands;

import connect_utils.CommandInfo;
import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;


public class RemoveLowerKeyCommand extends Command {
    RemoveLowerKeyCommand() {
        super("remove_lower_key", "id", "удаляет все элементы из коллекции, у которых id меньше заданного", null,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }

    /**
     * remove all elements with id that lower than id in args
     * <p>Modification time can be changed</p>
     *
     * @param commandController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException {
        long id = Long.parseLong(args[1]);
        boolean isMapModified = false;
        for (City city : commandController.getDataController().getMap().values()) {
            if (city.getId() < id) {
                isMapModified = true;
                commandController.getDataController().getMap().remove(city.getId());
            }
        }
        if (isMapModified) {
            commandController.getDataController().updateModificationTime();
            return "Коллекция была изменена.\n";
        }
        return "Коллекция осталась без изменений.\n";
    }
}
