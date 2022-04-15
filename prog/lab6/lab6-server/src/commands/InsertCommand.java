package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.IOException;

public class InsertCommand extends Command{
    InsertCommand() {
        super("insert", "id {element}", "добавляет элемент с определенным id", CommandInfo.SendInfo.CITY,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }
    /**
     * insert element with id in args
     * <p>Change modification time if command completes</p>
     * @param commandController that uses for program
     * @param args id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        Long id = Long.parseLong(args[1]);
        if (!City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемент с таким id уже существует в коллекции");
        commandController.sendOK();
        City city = (City) commandController.getConnectionController().receiveObject();
        if(city == null)
            throw new IncorrectArgumentException("город не был создан");
        city.setId(id);
        commandController.getDataController().putCityToMap(city);
        commandController.getDataController().updateModificationTime();
        return "Город был добавлен в коллекцию.\n";
    }
}
