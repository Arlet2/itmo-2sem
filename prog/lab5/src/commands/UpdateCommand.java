package commands;

import data_classes.City;
import data_classes.Coordinates;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class UpdateCommand extends Command{
    UpdateCommand() {
        super("update","id {element}", "обновляет значение элемента коллекции с определенным id");
    }

    @Override
    public void execute(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException {
        Long id = idValidator(commandController, args);
        if(City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с таким id не существует");
        System.out.println("Изменение полей города (любое поле может быть пропущено)...");
        City city = commandController.getDataController().createCityByUser(true);
        if(city == null)
            throw new IncorrectArgumentException("город не был создан");
        city.setId(id);
        deleteNullValues(commandController.getDataController().getMap().get(id), city);
        commandController.getDataController().putCityToMap(city);
        commandController.getDataController().updateModificationTime();
        System.out.println("Элемент с id "+id+" был обновлён.");
    }
}
