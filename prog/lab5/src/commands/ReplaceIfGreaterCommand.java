package commands;

import data_classes.City;
import data_classes.Coordinates;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class ReplaceIfGreaterCommand extends Command {
    private boolean isMapModified = false;
    ReplaceIfGreaterCommand () {
        super("replace_if_greater","id {element}","заменяет значение по id, если новое значение больше старого");
    }

    @Override
    public void execute (CommandController commandController, String[] args) throws MissingArgumentException, IncorrectArgumentException {
        long id = idValidator(commandController, args);
        if (City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с данным id не существует");
        City city = commandController.getDataController().createCityByUser(true);
        city.setId(id);
        deleteNullValues(commandController.getDataController().getMap().get(id), city);
        replaceCity(commandController.getDataController().getMap().get(id), city);
        commandController.getDataController().putCityToMap(city);
        if (isMapModified) {
            commandController.getDataController().updateModificationTime();
            System.out.println("Значение элемента с id " + id + " было обновлено.");
        }
    }
    // TODO: говнокод
    private void replaceCity (final City oldCity, final City newCity) {
        if (oldCity.getName().compareTo(newCity.getName()) > 0)
            newCity.setName(oldCity.getName());
        else
            isMapModified = true;
        if (oldCity.getCoordinates().getX() > newCity.getCoordinates().getX())
            newCity.getCoordinates().setX(oldCity.getCoordinates().getX());
        else
            isMapModified = true;
        if (oldCity.getCoordinates().getY() > newCity.getCoordinates().getY())
            newCity.getCoordinates().setY(oldCity.getCoordinates().getY());
        else
            isMapModified = true;
        if (oldCity.getEstablishmentDate().isAfter(newCity.getEstablishmentDate()))
            newCity.setEstablishmentDate(oldCity.getEstablishmentDate());
        else
            isMapModified = true;
        if (oldCity.getArea() > newCity.getArea())
            newCity.setArea(oldCity.getArea());
        else
            isMapModified = true;
        if (oldCity.getPopulation() > newCity.getPopulation())
            newCity.setPopulation(oldCity.getPopulation());
        else
            isMapModified = true;
        if (oldCity.getMetersAboveSeaLevel() > newCity.getMetersAboveSeaLevel())
            newCity.setMetersAboveSeaLevel(oldCity.getMetersAboveSeaLevel());
        else
            isMapModified = true;
        if (oldCity.getClimate().getValue() > newCity.getClimate().getValue())
            newCity.setClimate(oldCity.getClimate());
        else
            isMapModified = true;
        if (oldCity.getGovernment().getValue() > newCity.getGovernment().getValue())
            newCity.setGovernment(oldCity.getGovernment());
        else
            isMapModified = true;
        if (oldCity.getGovernor().getAge() > newCity.getGovernor().getAge())
            newCity.getGovernor().setAge(oldCity.getGovernor().getAge());
        else
            isMapModified = true;
        if (oldCity.getGovernor().getBirthday().isAfter(newCity.getGovernor().getBirthday()))
            newCity.getGovernor().setBirthday(oldCity.getGovernor().getBirthday());
        else
            isMapModified = true;
    }
}
