package commands;

import data_classes.City;

public class PrintAscendingCommand extends Command{
    PrintAscendingCommand() {
        super("print_ascending","","выводит элементы коллекции в порядке возрастания");
    }

    /**
     * print sorted collection
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
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
