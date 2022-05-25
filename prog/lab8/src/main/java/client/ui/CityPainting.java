package client.ui;

import data_classes.City;

import javax.swing.*;
import java.awt.*;

public class CityPainting extends JPanel {
    private final int x;
    private final int y;
    private final Color color;
    private final int size;

    CityPainting(City city) {
        super();
        this.x = (int) city.getCoordinates().getX();
        this.y = city.getCoordinates().getY();
        color = Color.WHITE;
        size = (int) city.getArea();
        setMaximumSize(new Dimension(size, size));
        setSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(color);
        g.fillRect(getX()+getWidth(), getY()+getHeight()/2, size, size);
    }
}
