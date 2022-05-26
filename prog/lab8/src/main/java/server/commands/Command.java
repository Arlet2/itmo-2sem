package server.commands;

import data_classes.City;
import data_classes.Coordinates;

import java.io.Serializable;

/**
 * abstract class for all commands
 * <p>Use executable interface</p>
 */
public abstract class Command implements Serializable, Executable {
    public enum CommandType {
        AUTH,
        CHANGE,
        INFO,
        EXIT,
        SCRIPT
    }
    /**
     * Types of argument for command
     */
    public enum ArgumentInfo {
        ID,
        FLOAT,
        INT,
        STRING,
        CLIMATE,
        GOVERNMENT
    }

    /**
     * Types of send info from client
     * city - client need to send city object
     * city_update - client need to send updating city object
     * commands - client need to send new commands
     * exit - client can interrupt program with this command
     */
    public enum SendInfo {
        CITY,
        CITY_UPDATE,
        COMMANDS
    }
    /**
     * Information that client need to send to server when command is executing
     */
    private SendInfo sendInfo;

    /**
     * Arguments that client need to check before he sends ones to server
     */
    private ArgumentInfo[] argInfo;

    /**
     * name of this command for usage
     */
    private final String name;

    private final CommandType type;

    protected Command(final String name, CommandType type) {
        this.name = name;
        this.type = type;
    }
    /**
     * Create new command that can execute on server
     *
     * @param name            of this command
     * @param sendInfo        of this command (for client)
     * @param argInfo         of this command (for client)
     */
    protected Command(final String name, SendInfo sendInfo,
                                 ArgumentInfo[] argInfo, CommandType type) {
        this(name, type);
        this.sendInfo = sendInfo;
        this.argInfo = argInfo;
    }

    /**
     * Delete null values in newCity.
     * <p>newCity will be <b>changed</b> after this method</p>
     *
     * @param oldCity where we take data
     * @param newCity where we put data
     */
    protected void deleteNullValues(final City oldCity, final City newCity) {
        if (newCity.getName() == null)
            newCity.setName(oldCity.getName());
        if (newCity.getCoordinates().getX() == Coordinates.X_INIT_VALUE)
            newCity.getCoordinates().setX(oldCity.getCoordinates().getX());
        if (newCity.getCoordinates().getY() == null)
            newCity.getCoordinates().setY(oldCity.getCoordinates().getY());
        if (newCity.getEstablishmentDate() == null)
            newCity.setEstablishmentDate(oldCity.getEstablishmentDate());
        if (newCity.getArea() == 0)
            newCity.setArea(oldCity.getArea());
        if (newCity.getPopulation() == 0)
            newCity.setPopulation(oldCity.getPopulation());
        if (newCity.getMetersAboveSeaLevel() == null)
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

    public CommandType getType() {
        return type;
    }

    public SendInfo getSendInfo() {
        return sendInfo;
    }

    public ArgumentInfo[] getArgInfo() {
        return argInfo;
    }
}
