package server.commands;

import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;
import server.data_control.PasswordManager;

import java.io.IOException;
import java.sql.SQLException;

public class LoginCommand extends Command {
    /**
     * Create new command that can execute on server
     */
    public LoginCommand() {
        super("login", null,
                new Command.ArgumentInfo[]{Command.ArgumentInfo.STRING, Command.ArgumentInfo.STRING}, CommandType.AUTH);
    }

    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        String[] strArgs = (String[]) args;
        try {
            if (!PasswordManager.checkPasswords(strArgs[2],
                    programController.getDataController().getDataBaseController().getUserSalt(strArgs[1]),
                    programController.getDataController().getDataBaseController().getUserPassword(strArgs[1])))
                throw new IncorrectArgumentException("wrong_password");
        } catch (SQLException e) {
            throw new IncorrectArgumentException("login_not_exist");
        }
        return "auth_success";
    }
}
