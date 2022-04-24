package commands;

import java.io.IOException;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "", "вызывает описание всех команд", null, null, false);
    }

    /**
     * print name, signature, description for all command
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        StringBuilder data = new StringBuilder();
        commandController.getAllCommands().forEach(command -> {
            if (!command.isServerCommand())
                data.append(String.format("%-50s - %-1s %n", command.getName() + " " + command.getSignature(),
                        command.getDescription()));
        });
        return data.toString();
    }
}
