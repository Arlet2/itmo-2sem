package commands;

public class SaveCommand extends Command{
    SaveCommand() {
        super("save","","сохраняет коллекцию в файл");
    }

    /**
     * Save collection to file (path in DataController)
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        commandController.getDataController().writeFile(commandController.getDataController().WORKING_PATH);
        System.out.println("Коллекция успешно сохранена.");
    }
}
