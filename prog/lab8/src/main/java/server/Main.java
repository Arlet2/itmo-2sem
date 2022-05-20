package server;

import connect_utils.DataTransferObject;
import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;
import server.commands.ProgramController;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {
    /**
     * Start program
     *
     * @param args is not using
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("DataTransferObject.Code.OK");
        list.add("DataTransferObject.Code.COMMAND");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        new ObjectOutputStream(stream).writeObject(list);
        DataTransferObject dto = new DataTransferObject(DataTransferObject.Code.REPLY, stream.toByteArray(),
                DataTransferObject.DataType.CITY);
        stream = new ByteArrayOutputStream();
        new ObjectOutputStream(stream).writeObject(dto);

        ByteBuffer buffer = ByteBuffer.wrap(stream.toByteArray());
        ByteArrayInputStream stream1 = new ByteArrayInputStream(buffer.array());
        dto = (DataTransferObject) new ObjectInputStream(stream1).readObject();
        ArrayList<String> list1 =
                (ArrayList<String>) new ObjectInputStream(new ByteArrayInputStream(dto.getDataBytes()))
                        .readObject();
        for(String code: list1)
            System.out.println(code);
        */
        Logger.createLogger();
        final ProgramController programController;
        try {
            programController = new ProgramController();
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
        Thread consoleListener = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String input;
            while (true) {
                input = scanner.nextLine().toLowerCase();
                if (input.equals("stop")) {
                    Logger.getLogger().log(Level.INFO, "Отключение сервера...");
                    programController.stop();
                    System.exit(0);
                } else
                    System.out.println("Незнакомая команда. Попробуйте stop");
            }
        }); // для остановки сервера
        consoleListener.start();
        programController.start();
    }
}
