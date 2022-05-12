package server.commands;

import connect_utils.CommandInfo;
import data_classes.City;
import exceptions.IncorrectArgumentException;
import server.connection_control.User;

import java.io.IOException;
import java.sql.SQLException;

public class InsertCommand extends Command {
    InsertCommand() {
        super("insert", "id {element}", "добавляет элемент с определенным id", CommandInfo.SendInfo.CITY,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }

    /**
     * insert element with id in args
     * <p>Change modification time if command completes</p>
     *
     * @param commandController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(User user, CommandController commandController, String[] args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        Long id = Long.parseLong(args[1]);
        if (!commandController.getDataController().checkUniqueID(id))
            throw new IncorrectArgumentException("элемент с таким id уже существует в коллекции");
        commandController.getConnectionController().getRequestController().sendOK();
        City city = commandController.getConnectionController().getRequestController().receiveCity();
        if (city == null)
            throw new IncorrectArgumentException("город не был создан");
        city.setId(id);
        try {
            commandController.getDataController().addCity(city, user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("не удалось добавить город в базу данных");
        }
        commandController.getDataController().updateModificationTime();
        return "Город был добавлен в коллекцию.\n";
    }
}
