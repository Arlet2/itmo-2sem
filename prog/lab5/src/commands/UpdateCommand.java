package commands;

import data_classes.City;
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
        updateCity(commandController.getDataController().getMap().get(id), city);
        commandController.getDataController().putCityToMap(city);
        commandController.getDataController().updateModificationTime();
        System.out.println("Элемент с id "+id+" был обновлён.");
    }
    private void updateCity (final City oldCity,City newCity) {
        if(newCity.getName() == null)
            newCity.setName(oldCity.getName());
        if(newCity.getCoordinates().getX() == -407f)
            newCity.getCoordinates().setX(oldCity.getCoordinates().getX());
        if(newCity.getCoordinates().getY() == null)
            newCity.getCoordinates().setY(oldCity.getCoordinates().getY());
        if(newCity.getArea() == 0)
            newCity.setArea(oldCity.getArea());
        if(newCity.getPopulation() == 0)
            newCity.setPopulation(oldCity.getPopulation());
        if(newCity.getMetersAboveSeaLevel() == null)
            newCity.setMetersAboveSeaLevel(oldCity.getMetersAboveSeaLevel());
        if(newCity.getEstablishmentDate() == null)
            newCity.setEstablishmentDate(oldCity.getEstablishmentDate());
        if(newCity.getClimate() == null)
            newCity.setClimate(oldCity.getClimate());
        if(newCity.getGovernment() == null)
            newCity.setGovernment(oldCity.getGovernment());
        if(newCity.getGovernor().getAge() == null)
            newCity.getGovernor().setAge(oldCity.getGovernor().getAge());
        if(newCity.getGovernor().getBirthday() == null)
            newCity.getGovernor().setBirthday(oldCity.getGovernor().getBirthday());
    }
}
