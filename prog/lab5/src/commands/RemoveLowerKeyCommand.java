package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class RemoveLowerKeyCommand extends Command{
    RemoveLowerKeyCommand() {
        super("remove_lower_key","id","удаляет все элементы из коллекции, у которых id меньше заданного");
    }

    /**
     * remove all elements with id that lower than id in args
     * <p>Modification time can be changed</p>
     * @param commandController that uses for program
     * @param args id
     * @throws MissingArgumentException if id is missing
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public void execute(CommandController commandController, String[] args) throws MissingArgumentException, IncorrectArgumentException {
        long id = idValidator(commandController, args);
        boolean isMapModified = false;
        for (City i: commandController.getDataController().getMap().values()) {
            if (i.getId() < id) {
                commandController.getDataController().getMap().remove(i.getId());
                isMapModified = true;
            }
        }
        if(isMapModified)
            commandController.getDataController().updateModificationTime();
    }
}
