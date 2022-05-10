package server.data_control;

import client.commands.CommandController;
import data_classes.City;
import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;

import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DataBaseController {
    //private final String DB_USER = "postgres";
    private final Connection connection;
    private final DataController dataController;
    DataBaseController (DataController dataController, String dbUrl, String dbUser) throws SQLException,
            MissingArgumentException, ConfigFileNotFoundException {
        this.dataController = dataController;
        connection = DriverManager.getConnection(dbUrl, dbUser, dataController.getFilesController().readDBPassword());
    }
    public ArrayList<City> getAllCities() throws SQLException {
        ResultSet results = connection.createStatement().executeQuery("SELECT * FROM cities;");
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ? WHERE id=?");
        ResultSet resultPs;
        City city;
        ArrayList<City> cities = new ArrayList<>();
        while (results.next()) {
            city = new City();
            city.setId(results.getLong("id"));
            city.setName(results.getString("name"));

            ps.setString(1, "coordinates");
            ps.setLong(2, city.getId());

            resultPs = ps.executeQuery();
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

            ps.setString(1, "humans");
            ps.setLong(2, city.getId());
            resultPs = ps.executeQuery();

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
}
