package server.data_control;

import client.commands.CommandController;
import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseController {
    //private final String DB_USER = "postgres";
    private final Connection connection;
    private final DataController dataController;
    DataBaseController (DataController dataController, String dbUrl, String dbUser) throws SQLException,
            MissingArgumentException, ConfigFileNotFoundException {
        this.dataController = dataController;
        connection = DriverManager.getConnection(dbUrl, dbUser, dataController.getFilesController().readDBPassword());
    }


}
