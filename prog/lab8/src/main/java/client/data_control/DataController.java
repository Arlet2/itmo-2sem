package client.data_control;

import client.AppController;
import data_classes.City;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class DataController {
    private final AppController appController;
    private final HashMap<Long, City> map = new HashMap<>();
    public DataController(AppController appController) {
        this.appController = appController;
    }
    public void updateMap(Collection<City> cities) throws IOException, ClassNotFoundException {
        map.clear();
        for (City city : cities)
            map.put(city.getId(), city);
        System.out.println("ОБНОВЛЕНИЕ!");
        appController.getUiController().updateData(cities);
    }

    public boolean isUniqueId(long id) {
        return !map.containsKey(id);
    }

    public HashMap<Long, City> getMap() {
        return map;
    }
}
