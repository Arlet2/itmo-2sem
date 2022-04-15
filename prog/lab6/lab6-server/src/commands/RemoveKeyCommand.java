package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;

public class RemoveKeyCommand extends Command{
    RemoveKeyCommand() {
        super("remove_key","id","удаляет элемент из коллекции с заданным ключом", null,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }

    /**
     * remove element with id from args
     * <p>Change modification time if command completes</p>
     * @param commandController that uses for program
     * @param args id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException {
        Long id = Long.parseLong(args[1]);
        if(City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с таким id не существует");
        commandController.getDataController().getMap().remove(id);
        commandController.getDataController().updateModificationTime();
        return "Элемент с id "+id+" был удалён.\n";
    }
}
