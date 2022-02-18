package commands;

public class SaveCommand extends Command{
    SaveCommand() {
        super("save","","сохраняет коллекцию в файл");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        commandController.getDataController().writeFile(commandController.getDataController().WORKING_PATH);
        System.out.println("Коллекция успешно сохранена.");
    }
}
