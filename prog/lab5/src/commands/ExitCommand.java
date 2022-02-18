package commands;

public class ExitCommand extends Command{
    ExitCommand() {
        super("exit","","завершает программу (без сохранения)");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        System.exit(0);
    }
}
