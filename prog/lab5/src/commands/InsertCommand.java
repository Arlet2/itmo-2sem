package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class InsertCommand extends Command{
    InsertCommand() {
        super("insert", "id {element}", "добавляет элемент с определенным id");
    }

    /**
     * insert element with id in args
     * <p>Change modification time if command completes</p>
     * @param commandController that uses for program
     * @param args id
     * @throws IncorrectArgumentException if id is incorrect
     * @throws MissingArgumentException if id is missing
     */
    @Override
    public void execute(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException {
        Long id = idValidator(commandController, args);
        if (!City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемент с таким id уже существует в коллекции");
        City city = commandController.getDataController().createCityByUser(false);
        if(city == null)
            throw new IncorrectArgumentException("город не был создан");
        city.setId(id);
        commandController.getDataController().putCityToMap(city);
        System.out.println("Город был добавлен в коллекцию.");
        commandController.getDataController().updateModificationTime();
    }
}
