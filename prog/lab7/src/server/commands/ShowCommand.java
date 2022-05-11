package server.commands;

import data_classes.City;

public class ShowCommand extends Command {
    ShowCommand() {
        super("show", "", "выводит коллекцию в консоль", null, null, false);
    }

    /**
     * Print collection
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) {
        if (commandController.getDataController().isMapEmpty()) {
            return "Коллекция пуста.\n";
        }
        StringBuilder data = new StringBuilder();
        int counter = 1;
        commandController.getDataController().readLock();
        for (City i : commandController.getDataController().getMap().values())
            data.append("Город ").append(counter++).append("\n").append(i).append("\n");
        commandController.getDataController().readUnlock();
        return data.toString();
    }
}