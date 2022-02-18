package commands;

import data_classes.City;

public class ShowCommand extends Command {
    ShowCommand() {
        super("show", "", "выводит коллекцию в консоль");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        int counter=1;
        if(commandController.getDataController().getMap().isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        for(City i: commandController.getDataController().getMap().values()) {
            System.out.println("Город "+counter+++"\n");
            System.out.println(i);
        }
    }
}