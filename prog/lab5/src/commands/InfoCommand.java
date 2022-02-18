package commands;

import java.time.LocalDateTime;

public class InfoCommand extends Command {
    InfoCommand() {
        super("info", "", "выводит информацию о коллекции");
    }

    @Override
    public void execute(CommandController commandController, String[] args) {
        System.out.println("Информация о коллекции:");
        System.out.println("Ключом выступает поле id");
        System.out.println("Время модификации коллекции: "+getDate(commandController.getDataController().getModificationTime()));
        System.out.println("Размер коллекции: "+commandController.getDataController().getMap().size());
        System.out.println("Ключи коллекции:");
        for (Long i: commandController.getDataController().getMap().keySet()) {
            System.out.print(i+" ");
        }
        System.out.println();
    }
    private String getDate(LocalDateTime localDateTime) {
        if(localDateTime == null)
            return "Коллекция не изменялась.";
        String strHour, strMin;
        if(localDateTime.getHour() < 10)
            strHour = "0"+localDateTime.getHour();
        else
            strHour = localDateTime.getHour()+"";
        if(localDateTime.getMinute() < 10)
            strMin = "0" + localDateTime.getMinute();
        else
            strMin = localDateTime.getMinute()+"";
        return localDateTime.getDayOfMonth()+"/"+localDateTime.getMonthValue()+"/"+localDateTime.getYear()+" "+strHour+":"+strMin;
    }
}
