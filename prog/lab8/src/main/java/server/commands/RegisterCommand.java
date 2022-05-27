package server.commands;

import exceptions.IncorrectArgumentException;
import server.ProgramController;
import server.connection_control.User;
import server.data_control.PasswordManager;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterCommand extends Command {
    public RegisterCommand() {
        super("register",
                null, new Command.ArgumentInfo[]{Command.ArgumentInfo.STRING,
                        Command.ArgumentInfo.STRING}, CommandType.AUTH);
    }

    @Override
    public String execute(User user, ProgramController programController, Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        String[] strArgs = (String[]) args;
        String password;
        String salt = PasswordManager.generateSalt();
        try {
            password = PasswordManager.createHash(strArgs[2] + salt);
            programController.getDataController().createUser(strArgs[1], password, salt);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IncorrectArgumentException("login_exist");
        }
        return "auth_success";
    }
}
