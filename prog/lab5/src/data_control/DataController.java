package data_control;

import data_classes.City;

import java.time.LocalDateTime;
import java.util.*;

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
     * that controls entering data by user
     */
    private final ConsoleController consoleController;

    /**
     * @param path of file where collection is
     */
    public DataController(final String path) {
        WORKING_PATH = path;
        map = new HashMap<>();
        fileController = new FileController(this);
        consoleController = new ConsoleController(this);
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
    public void readFile(final String path) {
        System.out.println("Считывания из файла по пути "+path+"...");
        fileController.readFromFile(path);
        System.out.println("Чтение завершено успешно.");
    }

    /**
     * Shell of FileController method
     * see FileController:writeFile()
     */
    public void writeFile(final String path) {
        System.out.println("Запись в файл по пути "+path+"...");
        fileController.writeFile(path);
    }

    /**
     * Shell of ConsoleController method
     * see ConsoleController:createCityByUser()
     */
    public City createCityByUser(final boolean isFieldsCanBeSkipped) {
        return consoleController.createCityByUser(isFieldsCanBeSkipped);
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

    /**
     * Sort collection and return it
     * @return sorted collection
     */
    public Collection<City> getSortMap() {
        ArrayList<City> arrayList = new ArrayList<>(map.values());
        arrayList.sort((o1, o2) -> {
            if(o1.getId() > o2.getId())
                return 1;
            else if(o1.getId() < o2.getId())
                return -1;
            return 0;
        });
        return arrayList;
    }

}
