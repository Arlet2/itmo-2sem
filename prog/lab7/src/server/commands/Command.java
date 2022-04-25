package server.commands;

import data_classes.City;
import data_classes.Coordinates;
import connect_utils.CommandInfo;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

/**
 * abstract class for all commands
 * <p>Use executable interface</p>
 */
public abstract class Command implements Executable {
    /**
     * name of this command for usage
     */
    private final String name;

    /**
     * description of this command (uses for help command)
     */
    private final String description;

    /**
     * signature of this command (uses for help command)
     */
    private final String signature;

    /**
     * Information that client need to send to server when command is executing
     */
    private final CommandInfo.SendInfo sendInfo;

    /**
     * Arguments that client need to check before he sends ones to server
     */
    private final CommandInfo.ArgumentInfo[] argInfo;

    /**
     * true if it's command that can use only from server console without client
     */
    private final boolean isServerCommand;

    /**
     * Create new command that can execute on server
     *
     * @param name            of this command
     * @param signature       that show arguments of this command (for help)
     * @param description     of this command (for help)
     * @param sendInfo        of this command (for client)
     * @param argInfo         of this command (for client)
     * @param isServerCommand if true client can't use it
     */
    Command(final String name, final String signature, final String description, CommandInfo.SendInfo sendInfo,
            CommandInfo.ArgumentInfo[] argInfo, boolean isServerCommand) {
        this.name = name;
        this.signature = signature;
        this.description = description;
        this.sendInfo = sendInfo;
        this.argInfo = argInfo;
        this.isServerCommand = isServerCommand;
    }

    /**
     * Validate id as argument in command
     *
     * @param args of command that enter
     * @return id from args
     * @throws IncorrectArgumentException if id is incorrect in args
     * @throws MissingArgumentException   if id is missing in args
     */
    protected long idValidator(String[] args) throws IncorrectArgumentException, MissingArgumentException {
        if (args.length < 2)
            throw new MissingArgumentException("id");
        if (args[1].isEmpty())
            throw new MissingArgumentException("id");
        long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IncorrectArgumentException("id - целое число");
        }
        if (id <= 0)
            throw new IncorrectArgumentException("id - число больше 0");
        return id;
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

    public String getSignature() {
        return signature;
    }

    public String getDescription() {
        return description;
    }

    public CommandInfo.SendInfo getSendInfo() {
        return sendInfo;
    }

    public CommandInfo.ArgumentInfo[] getArgInfo() {
        return argInfo;
    }

    public boolean isServerCommand() {
        return isServerCommand;
    }
}
