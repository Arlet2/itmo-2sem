package commands;

import data_classes.City;
import data_classes.Coordinates;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public abstract class Command implements Executable{
    private final String name;
    private final String description;
    private final String signature;
    Command (final String name,final String signature, final String description) {
        this.name = name;
        this.signature = signature;
        this.description = description;
    }
    protected long idValidator(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException {
        if(args.length < 2)
            throw new MissingArgumentException("id");
        if(args[1].isEmpty())
            throw new MissingArgumentException("id");
        long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IncorrectArgumentException("id - целое число");
        }
        if(id <= 0)
            throw new IncorrectArgumentException("id - число больше 0");
        return id;
    }
    protected void deleteNullValues (final City oldCity, final City newCity) {
        if (newCity.getName() == null)
            newCity.setName(oldCity.getName());
        if (newCity.getCoordinates().getX() == Coordinates.X_INIT_VALUE)
            newCity.getCoordinates().setX(oldCity.getCoordinates().getX());
        if (newCity.getCoordinates().getY()  == null)
            newCity.getCoordinates().setY(oldCity.getCoordinates().getY());
        if (newCity.getEstablishmentDate()  == null)
            newCity.setEstablishmentDate(oldCity.getEstablishmentDate());
        if (newCity.getArea() == 0)
            newCity.setArea(oldCity.getArea());
        if (newCity.getPopulation() == 0)
            newCity.setPopulation(oldCity.getPopulation());
        if (newCity.getMetersAboveSeaLevel()  == null)
            newCity.setMetersAboveSeaLevel(oldCity.getMetersAboveSeaLevel());
        if (newCity.getClimate() == null)
            newCity.setClimate(oldCity.getClimate());
        if (newCity.getGovernment() == null)
            newCity.setGovernment(oldCity.getGovernment());
        if (newCity.getGovernor().getAge() == null)
            newCity.getGovernor().setAge(oldCity.getGovernor().getAge());
        if (newCity.getGovernor().getBirthday() == null)
            newCity.getGovernor().setBirthday(oldCity.getGovernor().getBirthday());
    }
    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getDescription() {
        return description;
    }
}
