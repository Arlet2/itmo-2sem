package server.data_control;

import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;
import server.commands.CommandController;
import data_classes.City;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Control all data manipulations in files and console
 */
public class DataController {

    /**
     * Last time when collection was modificated
     */
    private LocalDateTime modificationTime;

    /**
     * Program's collection
     */
    private final HashMap<Long, City> map;

    /**
     * that controls program's execution
     */
    private final CommandController commandController;

    private final DataBaseController dataBaseController;

    private final FilesController filesController;
    public DataController(CommandController commandController) throws SQLException,
            MissingArgumentException, ConfigFileNotFoundException {
        this.commandController = commandController;
        filesController = new FilesController(this);
        map = new HashMap<>();
        dataBaseController = new DataBaseController(this, "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres");
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
     * Put city to collection (key is id)
     *
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

    public DataBaseController getDataBaseController() {
        return dataBaseController;
    }

    public FilesController getFilesController() {
        return filesController;
    }
}
