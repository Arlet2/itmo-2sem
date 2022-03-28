package commands;

public class ClearCommand extends Command{
    ClearCommand() {
        super("clear","","очищает элементы коллекции");
    }

    /**
     * Clear collection
     * <p>Change modification time</p>
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        commandController.getDataController().getMap().clear();
        System.out.println("Коллекция успешно очищена.");
        commandController.getDataController().updateModificationTime();
    }
}
