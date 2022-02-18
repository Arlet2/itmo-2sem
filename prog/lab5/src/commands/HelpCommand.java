package commands;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "","вызывает описание всех команд");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        for(Command i: commandController.getAllCommands())
            System.out.printf("%-50s - %-1s %n",i.getName()+" "+i.getSignature(),i.getDescription());
    }
}
