package server.commands;

import java.time.LocalDateTime;

public class InfoCommand extends Command {
    InfoCommand() {
        super("info", "", "выводит информацию о коллекции", null, null, false);
    }

    /**
     * Print information about collection:
     * <p>What is used as key, modification time, size, key set</p>
     *
     * @param commandController that uses for program
     * @param args              for command from console input (args[0] is program name)
     */
    @Override
    public String execute(CommandController commandController, String[] args) {
        StringBuilder data = new StringBuilder();
        data.append("Информация о коллекции:").append("\n");
        data.append("Ключом выступает поле id").append("\n");
        data.append("Время модификации коллекции: ").append(
                getDate(commandController.getDataController().getModificationTime())).append("\n");
        data.append("Размер коллекции: ").append(commandController.getDataController().getMap().size()).append("\n");
        data.append("Ключи коллекции:").append(" ");
        commandController.getDataController().getMap().keySet().forEach(id -> {
            data.append(id).append(" ");
        });
        data.append("\n");
        return data.toString();
    }

    /**
     * Convert modification date to String
     *
     * @param localDateTime of last modification
     * @return string with modification date
     */
    private String getDate(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return "Коллекция не изменялась.";
        String strHour, strMin;
        if (localDateTime.getHour() < 10)
            strHour = "0" + localDateTime.getHour();
        else
            strHour = localDateTime.getHour() + "";
        if (localDateTime.getMinute() < 10)
            strMin = "0" + localDateTime.getMinute();
        else
            strMin = localDateTime.getMinute() + "";
        return localDateTime.getDayOfMonth() + "/" + localDateTime.getMonthValue()
                + "/" + localDateTime.getYear() + " " + strHour + ":" + strMin;
    }
}
