package server.data_control;

import data_classes.City;
import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DataBaseController {
    private final Connection connection;
    private final DataController dataController;

    DataBaseController(DataController dataController, String dbUrl, String dbUser) throws SQLException,
            MissingArgumentException, ConfigFileNotFoundException {
        this.dataController = dataController;
        connection = DriverManager.getConnection(dbUrl, dbUser, dataController.getFilesController().readDBPassword());
    }

    public ArrayList<City> getAllCities() throws SQLException {
        ResultSet results = connection.createStatement().executeQuery("SELECT * FROM cities");
        PreparedStatement psCoords = connection.prepareStatement("SELECT * FROM coordinates WHERE id=?");
        PreparedStatement psHum = connection.prepareStatement("SELECT * FROM humans WHERE id=?");
        ResultSet resultPs;
        City city;
        ArrayList<City> cities = new ArrayList<>();
        while (results.next()) {
            city = new City();
            city.setId(results.getLong("id"));
            city.setName(results.getString("name"));
            psCoords.setLong(1, city.getId());

            resultPs = psCoords.executeQuery();
            resultPs.next();
            city.getCoordinates().setX(resultPs.getFloat("x"));
            city.getCoordinates().setY(resultPs.getInt("y"));

            city.setCreationDate(ZonedDateTime.parse(results.getString("creationdate")));
            city.setArea(results.getInt("area"));
            city.setPopulation(results.getInt("population"));
            city.setMetersAboveSeaLevel(results.getLong("metersabovesealevel"));
            city.setEstablishmentDate(LocalDate.parse(results.getString("establishmentdate")));
            city.setClimate(results.getString("climate"));
            city.setGovernment(results.getString("government"));

            psHum.setLong(1, city.getId());
            resultPs = psHum.executeQuery();
            resultPs.next();

            city.getGovernor().setAge(resultPs.getLong("age"));
            city.getGovernor().setBirthday(LocalDateTime.parse(resultPs.getString("birthday")));

            cities.add(city);
        }

        return cities;
    }

    public String getUserPassword(String login) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT password FROM users WHERE login = ?");
        ps.setString(1, login);
        ResultSet result = ps.executeQuery();
        result.next();
        return result.getString(1);
    }

    public void createUser(String login, String password, String salt) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO users VALUES(?, ?, ?)");
        ps.setString(1, login);
        ps.setString(2, password);
        ps.setString(3, salt);
        ps.execute();
    }

    public String getUserSalt(String login) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT salt FROM users WHERE login = ?");
        ps.setString(1, login);
        ResultSet result = ps.executeQuery();
        result.next();
        return result.getString(1);
    }

    public boolean isOwner(String login, long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT owner FROM cities WHERE id=?");
        ps.setLong(1, id);
        ResultSet resultSet = ps.executeQuery();
        resultSet.next();
        return login.equals(resultSet.getString(1));
    }

    public void addCity(City city, String login) throws SQLException {
        PreparedStatement ps;
        int sh = 0;
        //if (city.getId() != null) {
        ps = connection.prepareStatement("INSERT INTO cities(id, name, coordinatesid, creationdate, " +
                "area, population, metersabovesealevel, establishmentdate, climate, government, governor, owner)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setLong(1, city.getId());
        sh = 1;
        //}
        //else {
        //ps = connection.prepareStatement("INSERT INTO cities(name, coordinatesid, creationdate, " +
        //"area, population, metersabovesealevel, establishmentdate, climate, government, governor, owner)" +
        //" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        //}
        ps.setString(1 + sh, city.getName());

        PreparedStatement subPs = connection.prepareStatement("INSERT INTO coordinates(id, x, y) VALUES(?, ?, ?)");
        subPs.setLong(1, city.getId());
        subPs.setFloat(2, city.getCoordinates().getX());
        subPs.setInt(3, city.getCoordinates().getY());
        subPs.execute();

        ps.setLong(2 + sh, city.getId());
        ps.setString(3 + sh, city.getCreationDate().toString());
        ps.setLong(4 + sh, city.getArea());
        ps.setInt(5 + sh, city.getPopulation());
        ps.setLong(6 + sh, city.getMetersAboveSeaLevel());
        ps.setString(7 + sh, city.getEstablishmentDate().toString());
        ps.setObject(8 + sh, city.getClimate(), Types.OTHER);
        ps.setObject(9 + sh, city.getGovernment(), Types.OTHER);

        subPs = connection.prepareStatement("INSERT INTO humans(id, age, birthday) VALUES(?, ?, ?)");
        subPs.setLong(1, city.getId());
        subPs.setLong(2, city.getGovernor().getAge());
        subPs.setString(3, city.getGovernor().getBirthday().toString());
        subPs.execute();

        ps.setLong(10 + sh, city.getId());
        ps.setString(11 + sh, login);

        ps.execute();
    }

    public void updateCity(City city) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE cities " +
                "SET name = ?, " +
                "creationdate = ?, " +
                "area = ?, " +
                "population = ?, " +
                "metersabovesealevel = ?, " +
                "establishmentdate = ?, " +
                "climate = ?, " +
                "government = ? " +
                "WHERE id = ?");
        ps.setString(1, city.getName());

        PreparedStatement subPs = connection.prepareStatement("UPDATE coordinates SET x=?, y=? WHERE id = ?");
        subPs.setFloat(1, city.getCoordinates().getX());
        subPs.setInt(2, city.getCoordinates().getY());
        subPs.setLong(3, city.getId());
        subPs.execute();

        ps.setString(2, city.getCreationDate().toString());
        ps.setLong(3, city.getArea());
        ps.setInt(4, city.getPopulation());
        ps.setLong(5, city.getMetersAboveSeaLevel());
        ps.setString(6, city.getEstablishmentDate().toString());
        ps.setObject(7, city.getClimate(), Types.OTHER);
        ps.setObject(8, city.getGovernment(), Types.OTHER);
        ps.setLong(9, city.getId());

        subPs = connection.prepareStatement("UPDATE humans SET age=?, birthday=? WHERE id = ?");
        subPs.setLong(1, city.getGovernor().getAge());
        subPs.setString(2, city.getGovernor().getBirthday().toString());
        subPs.setLong(3, city.getId());
        subPs.execute();

        ps.execute();
    }

    public void removeCity(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM cities WHERE id=?");
        ps.setLong(1, id);
        ps.execute();
        PreparedStatement subPs = connection.prepareStatement("DELETE FROM coordinates WHERE id=?");
        subPs.setLong(1, id);
        subPs.execute();
        subPs = connection.prepareStatement("DELETE FROM humans WHERE id=?");
        subPs.setLong(1, id);
        subPs.execute();
    }

    public void clearAll(String login) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT FROM cities * WHERE owner=?");
        ps.setString(1, login);
        ResultSet resultSet = ps.executeQuery();
        ps = connection.prepareStatement("DELETE FROM cities * WHERE owner=?");
        ps.setString(1, login);
        ps.execute();
        PreparedStatement ps1 = connection.prepareStatement("DELETE FROM humans * WHERE id=?");
        ps = connection.prepareStatement("DELETE FROM coordinates * WHERE id=?");
        while (resultSet.next()) {
            ps.setLong(1, resultSet.getLong("id"));
            ps.execute();
            ps1.setLong(1, resultSet.getLong("id"));
            ps1.execute();
        }
    }

    public DataController getDataController() {
        return dataController;
    }
}
