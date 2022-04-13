import commands.CommandController;
import commands.SaveCommand;
import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            argsValidator(args);
        } catch (MissingArgumentException | IncorrectArgumentException e) {
            System.out.println("Ошибка инициализации коллекции: "+e.getMessage()+"\nПрограмма не может быть запущена");
            return;
        }
        final CommandController commandController;
        try {
            commandController = new CommandController(args[0]);
        } catch (IOException e) {
            System.out.println("Ошибка прочтения: файл не найден.");
            return;
        } catch (InvalidPathException e) {
            System.out.println("Ошибка прочтения: некорректный путь");
            return;
        }
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            String input;
            while(true) {
                input = scanner.nextLine().toLowerCase();
                if(input.equals("exit")) {
                    System.out.println("Отключение сервера...");
                    System.exit(0);
                }
                else if (input.equals("save"))
                    new SaveCommand().execute(commandController, null);
                else
                    System.out.println("Незнакомая команда. Попробуйте exit или help.");
            }
        }).start(); // для закрытия сервака
        commandController.startWork();
    }
    private static void argsValidator (final String[] args) throws MissingArgumentException, IncorrectArgumentException {
        if(args.length == 0)
            throw new MissingArgumentException("отсутствует имя файла");
        if(args.length > 1)
            throw new IncorrectArgumentException("слишком много аргументов");
    }
}
