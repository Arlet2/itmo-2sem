package commands;

import java.util.concurrent.atomic.AtomicInteger;

public class PrintAscendingCommand extends Command {
    PrintAscendingCommand() {
        super("print_ascending", "", "выводит элементы коллекции в порядке возрастания", null, null, false);
    }

    /**
     * print sorted collection
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) {
        AtomicInteger counter = new AtomicInteger(1);
        if (commandController.getDataController().getMap().isEmpty()) {
            return "Коллекция пуста.\n";
        }
        StringBuilder data = new StringBuilder();

        commandController.getDataController().getMap().values().stream().sorted().forEach(city ->
                data.append("Город ").append(counter.getAndIncrement()).append("\n").append(city).append("\n"));
        return data.toString();
    }
}
