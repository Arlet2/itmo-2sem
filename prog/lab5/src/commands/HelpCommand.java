package commands;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "","вызывает описание всех команд");
    }

    /**
     * print name, signature, description for all command
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        for(Command i: commandController.getAllCommands())
            System.out.printf("%-50s - %-1s %n",i.getName()+" "+i.getSignature(),i.getDescription());
    }
}
