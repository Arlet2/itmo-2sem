package server.commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;

import java.io.IOException;
import java.sql.SQLException;

public class ReplaceIfGreaterCommand extends Command {

    public ReplaceIfGreaterCommand() {
        super("replace_if_greater",
                Command.SendInfo.CITY_UPDATE,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.ID}, CommandType.CHANGE);
    }

    /**
     * replace all fields in element with id from args that be lower than new fields
     * <p>Modification time can be changed</p>
     *
     * @param programController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        City city = (City) args;
        long id = city.getId();
        if (programController.getDataController().isUniqueId(id))
            throw new IncorrectArgumentException("id_not_exist");
        try {
            if (!programController.getDataController().getDataBaseController().isOwner(user.getLogin(), id))
                throw new IncorrectArgumentException("not_owner");
            deleteNullValues(programController.getDataController().getMap().get(id), city);
            replaceCity(programController.getDataController().getMap().get(id), city);
            city.setOwner(user.getLogin());
            programController.getDataController().updateCity(city);
        } catch (SQLException e) {
            throw new IncorrectArgumentException("update_failed");
        }
        return "collection_modified";

    }

    /**
     * Change newCity's fields
     * <p>newCity's fields will change after this method</p>
     *
     * @param oldCity from collection
     * @param newCity from console entering
     */
    private void replaceCity(final City oldCity, City newCity) {
        if (oldCity.getName().compareTo(newCity.getName()) < 0) {
            oldCity.setName(newCity.getName());
        }
        if (oldCity.getCoordinates().getX() < newCity.getCoordinates().getX()) {
            oldCity.getCoordinates().setX(newCity.getCoordinates().getX());
        }
        if (oldCity.getCoordinates().getY() < newCity.getCoordinates().getY()) {
            oldCity.getCoordinates().setY(newCity.getCoordinates().getY());
        }
        if (oldCity.getEstablishmentDate().isBefore(newCity.getEstablishmentDate())) {
            oldCity.setEstablishmentDate(newCity.getEstablishmentDate());
        }
        if (oldCity.getArea() < newCity.getArea()) {
            oldCity.setArea(oldCity.getArea());
        }
        if (oldCity.getPopulation() < newCity.getPopulation()) {
            oldCity.setPopulation(newCity.getPopulation());
        }
        if (oldCity.getMetersAboveSeaLevel() < newCity.getMetersAboveSeaLevel()) {
            oldCity.setMetersAboveSeaLevel(newCity.getMetersAboveSeaLevel());
        }
        if (oldCity.getClimate() == null)
            oldCity.setClimate(newCity.getClimate());
        else if (newCity.getClimate() == null) ;
        else if (oldCity.getClimate().ordinal() < newCity.getClimate().ordinal()) {
            oldCity.setClimate(newCity.getClimate());
        }
        if (oldCity.getGovernment() == null)
            oldCity.setGovernment(newCity.getGovernment());
        else if (newCity.getGovernment() == null) ;
        if (oldCity.getGovernment().ordinal() < newCity.getGovernment().ordinal()) {
            oldCity.setGovernment(newCity.getGovernment());
        }
        if (oldCity.getGovernor().getAge() < newCity.getGovernor().getAge()) {
            oldCity.getGovernor().setAge(newCity.getGovernor().getAge());
        }
        if (oldCity.getGovernor().getBirthday().isBefore(newCity.getGovernor().getBirthday())) {
            oldCity.getGovernor().setBirthday(newCity.getGovernor().getBirthday());
        }
        newCity = oldCity;
    }
}
