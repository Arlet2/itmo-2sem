package commands;

import data_classes.City;

public class PrintFieldAscendingGovernment extends Command{
    PrintFieldAscendingGovernment() {
        super("print_field_ascending_government","","выводит значения поля government всех элементов в порядке возрастания");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        for (City i: commandController.getDataController().getSortMap()) {
            if(!i.getGovernmentString().equals(""))
                System.out.println("id "+i.getId()+": "+i.getGovernmentString());
        }
    }
}
