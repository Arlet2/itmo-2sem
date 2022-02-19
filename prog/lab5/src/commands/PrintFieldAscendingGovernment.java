package commands;

import data_classes.City;

public class PrintFieldAscendingGovernment extends Command{
    PrintFieldAscendingGovernment() {
        super("print_field_ascending_government","","выводит значения поля government всех элементов в порядке возрастания");
    }

    /**
     * print field government from all cities
     * @param commandController that uses for program
     * @param args for command from console input (args[0] is program name)
     */
    @Override
    public void execute(CommandController commandController, String[] args) {
        for (City i: commandController.getDataController().getSortMap()) {
            if(!i.getGovernmentString().equals(""))
                System.out.println("id "+i.getId()+": "+i.getGovernmentString());
        }
    }
}
