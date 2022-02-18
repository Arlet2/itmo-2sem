package commands;

public class HistoryCommand extends Command{
    HistoryCommand() {
        super("history","","выводит последние "+CommandController.MAX_COMMAND_IN_HISTORY+" команд");
    }

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
