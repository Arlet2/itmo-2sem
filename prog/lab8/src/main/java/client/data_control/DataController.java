package client.data_control;

import client.commands.CommandController;
import data_classes.City;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class DataController {
    private final CommandController commandController;
    private final HashMap<Long, City> map = new HashMap<>();
    public DataController(CommandController commandController) {
        this.commandController = commandController;
    }
    public void updateMap(Collection<City> cities) throws IOException, ClassNotFoundException {
        map.clear();
        for (City city : cities)
            map.put(city.getId(), city);
    }

    public boolean isUniqueId(long id) {
        return !map.containsKey(id);
    }

    public HashMap<Long, City> getMap() {
        return map;
    }
}
