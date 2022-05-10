package server;

import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;
import server.commands.CommandController;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;

/*
 *  TODO: добавить возможность добавления с авто id от бд
 */

public class Main {
    public static void main(String[] args) {
        Logger.createLogger();
        final CommandController commandController;
        try {
            commandController = new CommandController();
        } catch (SQLException e) {
            Logger.getLogger().log(Level.WARNING, "Ошибка подключения к базе данных.");
            e.printStackTrace();
            return;
        } catch (MissingArgumentException e) {
            Logger.getLogger().log(Level.WARNING, "Не найдены обязательные данные в файлах: " + e.getMessage());
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
                } else
                    System.out.println("Незнакомая команда. Попробуйте exit или help.");
            }
        }).start(); // для закрытия сервака
        commandController.start();
    }
}
