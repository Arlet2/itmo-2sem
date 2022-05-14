package server.data_control;

import exceptions.ConfigFileNotFoundException;
import data_classes.City;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final DataBaseController dataBaseController;

    ReentrantReadWriteLock mapLock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock authLock = new ReentrantReadWriteLock();

    public DataController() throws SQLException, ConfigFileNotFoundException {
        map = new HashMap<>();
        dataBaseController = new DataBaseController(this, "jdbc:postgresql://pg:5432/studs", "s338861");
        refreshMap();
    }

    public void refreshMap() throws SQLException {
        mapLock.writeLock().lock();
        map.clear();
        dataBaseController.getAllCities().forEach(city -> map.put(city.getId(), city));
        mapLock.writeLock().unlock();
    }

    public void addCity(City city, String login) throws SQLException {
        mapLock.writeLock().lock();
        dataBaseController.addCity(city, login);
        map.put(city.getId(), city);
        mapLock.writeLock().unlock();
    }

    public void clearMap(String login) throws SQLException {
        mapLock.writeLock().lock();
        dataBaseController.clearAll(login);
        refreshMap();
        mapLock.writeLock().unlock();
    }

    public void removeCity(Long id) throws SQLException {
        mapLock.writeLock().lock();
        dataBaseController.removeCity(id);
        map.remove(id);
        mapLock.writeLock().unlock();
    }

    public void updateCity(City city) throws SQLException {
        mapLock.writeLock().lock();
        dataBaseController.updateCity(city);
        map.put(city.getId(), city);
        mapLock.writeLock().unlock();
    }

    public void createUser(String login, String password, String salt) throws SQLException {
        authLock.writeLock().lock();
        dataBaseController.createUser(login, password, salt);
        authLock.writeLock().unlock();
    }

    public void readLock() {
        mapLock.readLock().lock();
    }

    public void readUnlock() {
        mapLock.readLock().unlock();
    }

    public HashMap<Long, City> getMap() {
        return map;
    }

    /**
     * Update time of collection modification
     * <p>Set time from LocalDateTime.now()</p>
     */
    public void updateModificationTime() {
        mapLock.writeLock().lock();
        modificationTime = LocalDateTime.now();
        mapLock.writeLock().unlock();
    }

    public boolean isUniqueId(Long id) {
        return !map.containsKey(id);
    }

    /**
     * Get modification time
     */
    public boolean isMapEmpty() {
        return map.isEmpty();
    }

    public LocalDateTime getModificationTime() {
        return modificationTime;
    }

    public DataBaseController getDataBaseController() {
        return dataBaseController;
    }
}
