package commands;

import data_classes.City;
import data_classes.Climate;
import exceptions.IncorrectArgumentException;

import java.io.IOException;

public class FilterGreaterThanClimateCommand extends Command {
    FilterGreaterThanClimateCommand() {
        super("filter_greater_than_climate", "climate",
                "выводит элементы, у которых значение поля climate больше заданного", null,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.CLIMATE}, false);
    }

    /**
     * print elements that have got climate that greater than climate from args
     *
     * @param commandController that uses for program
     * @param args              climate
     * @throws IncorrectArgumentException if climate is incorrect
     */
    @Override
    public String execute(CommandController commandController, String[] args)
            throws IncorrectArgumentException, IOException {
        Climate climate = null;
        for (Climate i : Climate.values()) {
            if (i.toString().equals(args[1].toUpperCase())) {
                climate = i;
                break;
            }
        }
        boolean isExist = false;
        for (City i : commandController.getDataController().getMap().values()) {
            if (i.getClimate() == null)
                continue;
            if (i.getClimate().ordinal() > climate.ordinal()) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            return "Таких элементов не существует в коллекции.\n";
        }
        StringBuilder data = new StringBuilder();
        Climate finalClimate = climate;
        commandController.getDataController().getMap().values().forEach(city -> {
            if (finalClimate == null && city.getClimate() != null)
                data.append(city).append("\n");
            else if (city.getClimate() == null && finalClimate == null) ;
            else if (city.getClimate().ordinal() > finalClimate.ordinal())
                data.append(city).append("\n");
        });
        return data.toString();
    }
}
