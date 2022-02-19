package commands;

public class ExitCommand extends Command{
    ExitCommand() {
        super("exit","","завершает программу (без сохранения)");
    }

    /**
     * exit from command
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        System.exit(0);
    }
}
