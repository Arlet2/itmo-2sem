package data_control;

import commands.CommandController;
import data_classes.City;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

/**
 * Control all data manipulations in files and console
 */
public class DataController {
    /**
     * File path where collection is
     */
    public final String WORKING_PATH;
    /**
     * Last time when collection was modificated
     */
    private LocalDateTime modificationTime;
    /**
     * Program's collection
     */
    private final HashMap<Long, City> map;
    /**
     * that controls reading/writing of files
     */
    private final FileController fileController;
    /**
     * that controls program's execution
     */
    private final CommandController commandController;
    /**
     * @param path of file where collection is
     */
    public DataController(final String path, CommandController commandController) throws IOException {
        this.commandController = commandController;
        WORKING_PATH = path;
        map = new HashMap<>();
        fileController = new FileController(this);
        readFile(WORKING_PATH);
    }

    /**
     * Update time of collection modification
     * <p>Set time from LocalDateTime.now()</p>
     */
    public void updateModificationTime() {
        modificationTime = LocalDateTime.now();
    }

    /**
     * Get modification time
     */
    public LocalDateTime getModificationTime() {
        return modificationTime;
    }

    /**
     * Shell of FileController method
     * see FileController:readFromFile()
     */
    public void readFile(final String path) throws IOException {
        commandController.getLogger().log(Level.INFO, "Считывания из файла по пути "+path+"...");
        fileController.readFromFile(path);
        commandController.getLogger().log(Level.INFO, "Чтение завершено успешно.");
    }

    /**
     * Shell of FileController method
     * see FileController:writeFile()
     */
    public void writeFile(final String path) {
        commandController.getLogger().log(Level.INFO, "Запись в файл по пути "+path+"...");
        fileController.writeFile(path);
    }

    /**
     * Put city to collection (key is id)
     * @param city that we put in collection
     */
    public void putCityToMap(final City city) {
        map.put(city.getId(), city);
    }

    /**
     * Get collection
     */
    public HashMap<Long, City> getMap() {
        return map;
    }


    public CommandController getCommandController() {
        return commandController;
    }
}
