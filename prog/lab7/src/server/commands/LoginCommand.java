package server.commands;

import connect_utils.CommandInfo;
import exceptions.IncorrectArgumentException;
import server.data_control.PasswordController;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;

public class LoginCommand extends Command {
    /**
     * Create new command that can execute on server
     */
    LoginCommand() {
        super("login", "username password", "выполняет авторизацию пользователя", null,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.STRING, CommandInfo.ArgumentInfo.STRING},
                false);
    }

    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException,
            IOException, ClassNotFoundException {
        try {
            if (!PasswordController.checkPasswords(args[1],
                    commandController.getDataController().getDataBaseController().getUserSalt(args[1]),
                    commandController.getDataController().getDataBaseController().getUserPassword(args[1])))
                throw new IncorrectArgumentException("неверный пароль пользователя");
        } catch (SQLException e) {
            throw new IncorrectArgumentException("пользователь не найден.");
        }
        return "Пользователь успешно авторизован.\n";
    }
}
