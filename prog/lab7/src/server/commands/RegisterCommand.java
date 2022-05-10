package server.commands;

import connect_utils.CommandInfo;
import exceptions.IncorrectArgumentException;
import server.data_control.PasswordController;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterCommand extends Command{
    RegisterCommand() {
        super("register", "login password", "регистрирует нового пользователя в системе",
                null, new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.STRING,
                        CommandInfo.ArgumentInfo.STRING}, false);
    }

    @Override
    public String execute(CommandController commandController, String[] args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        String password;
        String salt = PasswordController.generateSalt();
        try {
            password = PasswordController.createHash(args[1]+salt);
            commandController.getDataController().getDataBaseController().createUser(args[1], password, salt);
        } catch (SQLException e) {
            throw new IncorrectArgumentException("Пользователь с таким логином уже существует.");
        }
        return "Пользователь был успешно зарегистрирован.\n";
    }
}
