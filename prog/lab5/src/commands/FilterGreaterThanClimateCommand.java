package commands;

import data_classes.City;
import data_classes.Climate;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public class FilterGreaterThanClimateCommand extends Command{
    FilterGreaterThanClimateCommand() {
        super("filter_greater_than_climate","climate","выводит элементы, у которых значение поля climate больше заданного");
    }

    @Override
    public void execute(CommandController commandController, String[] args) throws MissingArgumentException, IncorrectArgumentException {
        if(args.length < 2)
            throw new MissingArgumentException("climate");
        if(args[1].isEmpty())
            throw new MissingArgumentException("climate");
        Climate climate = null;
        for (Climate i: Climate.values()) {
            if (i.toString().equals(args[1].toUpperCase()))
                climate = i;
        }
        if (climate == null)
            throw new IncorrectArgumentException("некорректное значение climate");
        boolean isExist = false;
        for (City i: commandController.getDataController().getMap().values()) {
            if (i.getClimate() == null)
                continue;
            if (i.getClimate().getValue() > climate.getValue()) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            System.out.println("Таких элементов не существует в коллекции.");
            return;
        }
        for (City i: commandController.getDataController().getMap().values()) {
            if (i.getClimate() == null)
                continue;
            if (i.getClimate().getValue() > climate.getValue())
                System.out.println(i);
        }
    }
}
