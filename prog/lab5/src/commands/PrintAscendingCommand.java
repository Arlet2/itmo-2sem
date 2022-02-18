package commands;

import data_classes.City;

public class PrintAscendingCommand extends Command{
    PrintAscendingCommand() {
        super("print_ascending","","выводит элементы коллекции в порядке возрастания");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        int counter=1;
        if(commandController.getDataController().getMap().isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        for(City i: commandController.getDataController().getSortMap()) {
            System.out.println("Город "+counter+++"\n");
            System.out.println(i);
        }
    }
}
