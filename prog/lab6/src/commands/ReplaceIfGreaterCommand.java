package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class ReplaceIfGreaterCommand extends Command {
    private boolean isMapModified = false;
    ReplaceIfGreaterCommand () {
        super("replace_if_greater","id {element}","заменяет значение по id, если новое значение больше старого");
    }

    /**
     * replace all fields in element with id from args that be lower than new fields
     * <p>Modification time can be changed</p>
     * @param commandController that uses for program
     * @param args id
     * @throws MissingArgumentException if id is missing
     * @throws IncorrectArgumentException if id is incorrect
     */
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

    /**
     * Change newCity's fields
     * <p>newCity's fields will change after this method</p>
     * @param oldCity from collection
     * @param newCity from console entering
     */
    private void replaceCity (final City oldCity, City newCity) {
        if (oldCity.getName().compareTo(newCity.getName()) < 0) {
            oldCity.setName(newCity.getName());
            isMapModified = true;
        }
        if (oldCity.getCoordinates().getX() < newCity.getCoordinates().getX()) {
            oldCity.getCoordinates().setX(newCity.getCoordinates().getX());
            isMapModified = true;
        }
        if (oldCity.getCoordinates().getY() < newCity.getCoordinates().getY()) {
            oldCity.getCoordinates().setY(newCity.getCoordinates().getY());
            isMapModified = true;
        }
        if (oldCity.getEstablishmentDate().isBefore(newCity.getEstablishmentDate())) {
            oldCity.setEstablishmentDate(newCity.getEstablishmentDate());
            isMapModified = true;
        }
        if (oldCity.getArea() < newCity.getArea()) {
            oldCity.setArea(oldCity.getArea());
            isMapModified = true;
        }
        if (oldCity.getPopulation() < newCity.getPopulation()) {
            oldCity.setPopulation(newCity.getPopulation());
            isMapModified = true;
        }
        if (oldCity.getMetersAboveSeaLevel() < newCity.getMetersAboveSeaLevel()) {
            oldCity.setMetersAboveSeaLevel(newCity.getMetersAboveSeaLevel());
            isMapModified = true;
        }
        if (oldCity.getClimate().getValue() < newCity.getClimate().getValue()) {
            oldCity.setClimate(newCity.getClimate());
            isMapModified = true;
        }
        if (oldCity.getGovernment().getValue() > newCity.getGovernment().getValue()) {
            oldCity.setGovernment(newCity.getGovernment());
            isMapModified = true;
        }
        if (oldCity.getGovernor().getAge() > newCity.getGovernor().getAge()) {
            oldCity.getGovernor().setAge(newCity.getGovernor().getAge());
            isMapModified = true;
        }
        if (oldCity.getGovernor().getBirthday().isBefore(newCity.getGovernor().getBirthday())) {
            oldCity.getGovernor().setBirthday(newCity.getGovernor().getBirthday());
            isMapModified = true;
        }
        newCity = oldCity;
    }
}
