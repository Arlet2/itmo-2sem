package commands;

public class ClearCommand extends Command{
    ClearCommand() {
        super("clear","","очищает элементы коллекции");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        commandController.getDataController().getMap().clear();
        System.out.println("Коллекция успешно очищена.");
        commandController.getDataController().updateModificationTime();
    }
}
