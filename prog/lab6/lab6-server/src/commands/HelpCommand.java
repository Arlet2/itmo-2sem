package commands;

import java.io.IOException;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "","вызывает описание всех команд", null,null, false);
    }

    /**
     * print name, signature, description for all command
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IOException {
        StringBuilder data = new StringBuilder();
        for(Command i: commandController.getAllCommands()) {
            if (!isServerCommand())
                data.append(String.format("%-50s - %-1s %n", i.getName() + " " + i.getSignature(), i.getDescription()));
        }
        return data.toString();
    }
}
