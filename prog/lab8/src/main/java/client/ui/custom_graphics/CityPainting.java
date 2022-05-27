package client.ui.custom_graphics;

import data_classes.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CityPainting extends JPanel {
    private final int x;
    private final int y;
    private final long id;
    private final Color color;
    private int size;
    private final GraphicMap map;

    public CityPainting(GraphicMap map, City city) {
        super();
        this.map = map;
        this.x = (int) city.getCoordinates().getX();
        this.y = city.getCoordinates().getY();
        id = city.getId();
        if (id%2 ==0)
            color = Color.BLACK;
        else
            color = Color.BLUE;
        size = (int) city.getArea();
        setListeners();
        resize();
    }
    public void setListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(id);
            }
        });
    }
    public void resize() {
        System.out.println("Сделали ресайз..");
        size = (int)(size*map.getZoom());
        System.out.println(size);
        if (size <= 0)
            size = 1;
        setBounds(x+map.getMapX(), y+map.getMapY(), (size), size);
        setMinimumSize(new Dimension(size, size));
        setSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setBorder(BorderFactory.createBevelBorder(1));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, size, size);
    }
}
