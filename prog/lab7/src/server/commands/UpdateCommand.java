package server.commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import connect_utils.CommandInfo;
import server.connection_control.ConnectionController;

import java.io.IOException;

public class UpdateCommand extends Command {
    UpdateCommand() {
        super("update", "id {element}", "обновляет значение элемента коллекции с определенным id",
                CommandInfo.SendInfo.CITY_UPDATE,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.ID}, false);
    }

    /**
     * update element with id from args by new from console input
     *
     * @param commandController that uses for program
     * @param args              id
     * @throws IncorrectArgumentException if id is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        Long id = Long.parseLong(args[1]);
        if (City.checkUniqueID(id, commandController.getDataController().getMap()))
            throw new IncorrectArgumentException("элемента с таким id не существует");
        commandController.sendOK();
        City city = (City) commandController.getConnectionController().receiveObject();
        if (city == null)
            throw new IncorrectArgumentException("город не был создан");
        city.setId(id);
        deleteNullValues(commandController.getDataController().getMap().get(id), city);
        commandController.getDataController().putCityToMap(city);
        commandController.getDataController().updateModificationTime();
        return "Элемент с id " + id + " был обновлён.\n";
    }
}
