package commands;

import java.io.IOException;

public class ExitCommand extends Command{
    ExitCommand() {
        super("exit","","завершает программу (без сохранения)", CommandInfo.SendInfo.EXIT,null, false);
    }

    /**
     * exit from command
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        commandController.getConnectionController().disconnect();
        System.out.println("Пользователь отключился от сервера.");
        new SaveCommand().execute(commandController, null);
        return null;
    }
}
