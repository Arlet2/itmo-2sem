package client.commands;

import exceptions.IncorrectArgumentException;
import server.commands.Command;
import server.commands.ProgramController;
import server.connection_control.User;

import java.io.IOException;

public class FilterGreaterThanClimateCommand extends Command {
    FilterGreaterThanClimateCommand() {
        super("filter_greater_than_climate", "climate",
                "выводит элементы, у которых значение поля climate больше заданного", null,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.CLIMATE}, CommandType.INFO);
    }


    @Override
    public String execute(User user, ProgramController programController, String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        return null;
    }
}
