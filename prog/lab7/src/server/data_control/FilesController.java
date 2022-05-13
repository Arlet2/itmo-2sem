package server.data_control;

import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesController {

    public static String readDBPassword() throws MissingArgumentException, ConfigFileNotFoundException {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream("db.con"));
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException("Не был найден файл конфигурации базы данных.");
        }
        StringBuilder s = new StringBuilder();
        while (scanner.hasNextLine()) {
            s.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        Matcher matcher = Pattern.compile("(?<=password:\\s{0,10})[^\\s]+", Pattern.CASE_INSENSITIVE).matcher(s.toString());
        if (matcher.find())
            return s.substring(matcher.start(), matcher.end());
        else
            throw new MissingArgumentException("в файле конфигурации базы данных не был найден пароль. " +
                    "Добавьте в этот файл строку \"password: ***\"");
    }

    public static int readConfigPort() throws MissingArgumentException, ConfigFileNotFoundException {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream("config.excalibbur"));
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException("Не был найден файл конфигурации config.excalibbur. " +
                    "Добавьте его, указав порт следующим образом:\n" +
                    "port: 1234");
        }
        StringBuilder s = new StringBuilder();
        while (scanner.hasNextLine())
            s.append(scanner.nextLine()).append("\n");
        scanner.close();
        Matcher matcher = Pattern.compile("(?<=port:)\\d+|(?<=port:\\s)\\d+|(?<=port:\\s{2})\\d+",
                Pattern.CASE_INSENSITIVE).matcher(s.toString());
        if (matcher.find())
            return Integer.parseInt(s.substring(matcher.start(), matcher.end()));
        else
            throw new MissingArgumentException("в файле конфигурации подключения не был найден порт. " +
                    "Добавьте в файл строку типа \"port: 1234\"");
    }
}
