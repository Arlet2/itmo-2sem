package commands;

public class HistoryCommand extends Command{
    HistoryCommand() {
        super("history","","выводит последние "+CommandController.MAX_COMMANDS_IN_HISTORY +" команд");
    }

    /**
     * print history of command that was used in console
     * <p>Max commands in history can be changed in CommandController</p>
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        if (commandController.getHistory().isEmpty()) {
            System.out.println("История команд пуста.");
            return;
        }
        for (int i=0;i<commandController.getHistory().size();i++)
            System.out.print((i+1)+") "+commandController.getHistory().get(i).getName()+" ");
        System.out.println();
    }
}
