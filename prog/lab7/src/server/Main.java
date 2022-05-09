package server;

import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;
import server.commands.CommandController;
import server.commands.SaveCommand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;

/*
    TODO: Запихать в FilesController абсолютно всё взаимодействие с файлами (для клиента также)
 */
public class Main {
    public static void main(String[] args) {
        /*
        MessageDigest alg = MessageDigest.getInstance("sha-256");
        alg.digest("hello".getBytes());
        try {
            if (connection.createStatement().execute("SELECT * FROM coordinates;"))
                System.out.println("супер.");
            ResultSet set = connection.createStatement().executeQuery("SELECT * FROM coordinates;");
            while (set.next()) {
                System.out.println(set.getInt("id"));
                System.out.println(set.getFloat("x"));
                System.out.println(set.getInt("y"));
            }
            set = connection.createStatement().executeQuery("INSERT INTO coordinates (x, y) VALUES (13.3, 415);");
            while(set.next()) {
                System.out.println(set.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        Logger.createLogger();
        final CommandController commandController;
        try {
            commandController = new CommandController();
        } catch (SQLException e) {
            Logger.getLogger().log(Level.WARNING, "Ошибка подключения к базе данных.");
            return;
        } catch (MissingArgumentException e) {
            Logger.getLogger().log(Level.WARNING, "Не найдены обязательные данные в файлах: "+e.getMessage());
            return;
        } catch (ConfigFileNotFoundException e) {
            Logger.getLogger().log(Level.WARNING, e.getMessage());
            return;
        }
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String input;
            while (true) {
                input = scanner.nextLine().toLowerCase();
                if (input.equals("exit")) {
                    System.out.println("Отключение сервера...");
                    System.exit(0);
                } else if (input.equals("save"))
                    new SaveCommand().execute(commandController, null);
                else
                    System.out.println("Незнакомая команда. Попробуйте exit или help.");
            }
        }).start(); // для закрытия сервака
        commandController.start();
    }
}
