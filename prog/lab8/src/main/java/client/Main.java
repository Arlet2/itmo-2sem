package client;

import client.ui.UIController;
import client.ui.custom_graphics.CityPainting;
import client.ui.custom_graphics.GraphicMap;
import data_classes.City;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class Main {
    /**
     * Start execution of program
     *
     * @param args do not use
     */
    public static void main(String[] args) {
        /*
        int sizeWidth = 900;
        int sizeHeight = 900;
        JFrame jFrame = new JFrame();
        jFrame.setMinimumSize(new Dimension(sizeWidth, sizeHeight));
        jFrame.setBounds((1920-sizeWidth)/2, (1080-sizeHeight)/2, sizeWidth, sizeHeight);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphicMap map = new GraphicMap(jFrame);
        Collection<City> cities = new ArrayList<>();
        City city = new City();
        city.setId(1L);
        city.getCoordinates().setX(200);
        city.getCoordinates().setY(100);
        city.setArea(100);
        city.setOwner("mama");

        cities.add(city);
        City city1 = new City();
        city1.setId(2L);
        city1.getCoordinates().setX(100);
        city1.getCoordinates().setY(100);
        city1.setArea(50);
        cities.add(city1);

        map.loadCities(cities);
        //city.getCoordinates().setX(200);
        //map.loadCities(cities);

        jFrame.add(map);

        jFrame.setVisible(true);
        jFrame.pack();
        // */
        Locale.setDefault(Locale.ENGLISH);
        try {
            AppController appController = new AppController();
            appController.startWork();
        } catch (MissingArgumentException | ConfigFileNotFoundException | ConnectionException e) {
            UIController.showErrorDialog(e.getMessage());
        }
        // */
    }
}
