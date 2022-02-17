package data_control;

import data_classes.City;

import java.util.*;

public class DataController {
    private final HashMap<Long, City> map;
    private final FileController fileController;
    private final ConsoleController consoleController;
    public DataController() {
        map = new HashMap<>();
        fileController = new FileController(this);
        consoleController = new ConsoleController(this);
    }
    public void readFile(final String path) {
        System.out.println("Считывания из файла по пути "+path+".");
        fileController.readFromFile(path);
    }
    public void writeFile(final String path) {
        System.out.println("Запись в файл по пути "+path+".");
        fileController.writeFile(path);
    }
    public City createCityByUser() {
        return consoleController.createCityByUser();
    }
    public void putCityToMap(final City city) {
        map.put(city.getId(), city);
    }
    public HashMap<Long, City> getMap() {
        return map;
    }
    public Collection<City> sortMap() {
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
