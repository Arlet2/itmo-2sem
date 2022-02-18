package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class RemoveKeyCommand extends Command{
    RemoveKeyCommand() {
        super("remove_key","id","удаляет элемент из коллекции с заданным ключом");
    }

    @Override
    public void execute(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException {
        Long id = idValidator(commandController, args);
        if(City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с таким id не существует");
        commandController.getDataController().getMap().remove(id);
        commandController.getDataController().updateModificationTime();
        System.out.println("Элемент с id "+id+" был удалён.");
    }
}
