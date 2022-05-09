package server;

import server.commands.CommandController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

public class Logger {
    private static java.util.logging.Logger logger;

    /**
     * Create logger with config from file logger.config
     */
    public static void createLogger() {
        try (FileInputStream ins = new FileInputStream("logger.config")) {
            LogManager.getLogManager().readConfiguration(ins);
        } catch (FileNotFoundException e) {
            System.out.println("Файл конфигурации логгера не найден.");
        } catch (IOException e) {
            System.out.println("Не удалось открыть файл конфигурации логгера.");
        }
        logger = java.util.logging.Logger.getLogger(CommandController.class.getName());
    }

    public static java.util.logging.Logger getLogger() {
        return logger;
    }
}
