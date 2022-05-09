package server.commands;

import connect_utils.CommandInfo;
import exceptions.IncorrectArgumentException;

import java.io.IOException;

public class LoginCommand extends Command {
    /**
     * Create new command that can execute on server
     */
    LoginCommand() {
        super("login", "username password", "выполняет авторизацию пользователя", CommandInfo.SendInfo.AUTH,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.STRING, CommandInfo.ArgumentInfo.STRING},
                false);
    }

    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException,
            IOException, ClassNotFoundException {

        return "Пользователь успешно авторизован.";
    }
}
