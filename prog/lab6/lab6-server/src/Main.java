import commands.CommandController;
import connection_control.ConnectionController;
import connection_control.Request;
import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /*
        try {
            argsValidator(args);
        } catch (MissingArgumentException | IncorrectArgumentException e) {
            System.out.println("Ошибка инициализации коллекции: "+e.getMessage()+"\nПрограмма не может быть запущена");
            return;
        }
        CommandController cc = new CommandController(new DataController(args[0]));
        cc.listenConsole();
         */
        CommandController cc = new CommandController(new DataController(args[0]));
    }
    private static void argsValidator (final String[] args) throws MissingArgumentException, IncorrectArgumentException {
        if(args.length == 0)
            throw new MissingArgumentException("отсутствует имя файла");
        if(args.length > 1)
            throw new IncorrectArgumentException("слишком много аргументов");
    }
}
