package client.ui.custom_graphics;

import client.ui.AbstractWindow;
import client.ui.UIController;
import data_classes.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class CityPainting extends JPanel {
    private final int x;
    private final int y;
    private final long id;
    private final Color color;
    private int currentSize;
    private final int size;
    private final GraphicMap map;

    public CityPainting(GraphicMap map, City city, boolean isNew) {
        super();
        this.map = map;
        this.x = (int) city.getCoordinates().getX();
        this.y = city.getCoordinates().getY();
        id = city.getId();
        color = chooseColor(city.getOwner());
        map.addColorToCollection(city.getOwner(), color);
        size = (int) city.getArea();
        currentSize = (int) city.getArea();
        setListeners();
        resize(isNew);
    }

    private Color chooseColor(String owner) {
        Color color = map.getColorByOwner(owner);
        if (color != null)
            return color;
        Random random = new Random();
        do {
            color = new Color(Math.abs(random.nextInt() % 256),
                    Math.abs(random.nextInt() % 256), Math.abs(random.nextInt() % 256));
        } while (map.isThisColorOnCollection(color));
        return color;
    }

    public void setListeners() {
        addMouseListener(new MouseAdapter() {
            int rowIndex = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rowIndex = findById();
                    StringBuilder stringBuilder = new StringBuilder();
                    String name = (String)map.getWindow().getTable().getModel().getValueAt(rowIndex, 1);
                    String coordinateX = (String)map.getWindow().getTable().getModel().getValueAt(rowIndex, 2);
                    String coordinateY = (String)map.getWindow().getTable().getModel().getValueAt(rowIndex, 3);
                    String area = (String)map.getWindow().getTable().getModel().getValueAt(rowIndex, 5);
                    String population = (String)map.getWindow().getTable().getModel().getValueAt(rowIndex, 6);
                    stringBuilder.append(map.getWindow().getString("current_city_id")).append(": ")
                            .append(id).append('\n');
                    stringBuilder.append(map.getWindow().getString("current_city_name")).append(": ")
                            .append(name).append('\n');
                    stringBuilder.append(map.getWindow().getString("current_city_x")).append(": ")
                            .append(coordinateX).append('\n');
                    stringBuilder.append(map.getWindow().getString("current_city_y")).append(": ")
                            .append(coordinateY).append('\n');
                    stringBuilder.append(map.getWindow().getString("current_city_area")).append(": ")
                            .append(area).append('\n');
                    stringBuilder.append(map.getWindow().getString("current_city_population")).append(": ")
                            .append(population).append('\n');
                    UIController.showInfoDialog(stringBuilder.toString(),
                            map.getWindow().getString("current_city_window_name"));
                }
                else if (e.getButton() == MouseEvent.BUTTON1) {
                    rowIndex = findById();
                    map.getWindow().getTable().setRowSelectionInterval(rowIndex, rowIndex);
                    map.getWindow().selectionAction();
                    map.getWindow().getTabs().setSelectedIndex(0);
                }
            }
            private int findById() {
                for (int i = 0; i < map.getWindow().getTable().getModel().getRowCount(); i++) {
                    if (Long.parseLong(((String) map.getWindow().getTable().getModel().getValueAt(i, 0))
                            .replaceAll("[,.\\s]", "")) == id) {
                        return i;
                    }
                }
                return 0;
            }
        });
    }

    public void resize(boolean isNew) {
        if (isNew) {
            /*
            new Timer(100, new ActionListener() {
                final int frames = 100;
                int currentFrame = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("КАДР КАДР КАДР!");
                    if (currentFrame == frames) {
                        CityPainting.this.notify();
                        ((Timer) e.getSource()).stop();
                    }
                    currentFrame++;
                    setBounds(x + map.getMapX() - currentSize / 2, y + map.getMapY() - currentSize / 2,
                            currentSize * currentFrame / 98, currentSize * currentFrame / 98);
                    setSize();
                    repaint();
                }
            }).start();
            try {
                wait();
            } catch (InterruptedException ignored) {

            }
            */
        }
        if ((int) (currentSize * map.getZoom()) > 1) {
            currentSize = (int) (currentSize * map.getZoom());
        }
        if (map.getZoomCounter() == 0)
            currentSize = size;
        move(true);
    }

    public void move(boolean isUsedZoom) {
        if (!isUsedZoom) {
            setBounds(x + map.getMapX() - currentSize / 2, y + map.getMapY() - currentSize / 2,
                    currentSize, currentSize);
            if (map.getZoomCounter() == 0) {
                setBounds(x + map.getMapX() - currentSize / 2,
                        y + map.getMapY() - currentSize / 2,
                        currentSize, currentSize);
            }
        } else {
            setBounds(x + map.getMapX() - currentSize / 2,
                    y + map.getMapY() - currentSize / 2,
                    currentSize, currentSize);
            if (map.getZoomCounter() == 0) {
                setBounds(x + map.getMapX() - currentSize / 2,
                        y + map.getMapY() - currentSize / 2,
                        currentSize, currentSize);
            }
        }
        setSize();
        repaint();
    }

    private void setSize() {
        setMinimumSize(new Dimension(currentSize, currentSize));
        setSize(new Dimension(currentSize, currentSize));
        setPreferredSize(new Dimension(currentSize, currentSize));
        setMaximumSize(new Dimension(currentSize, currentSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, currentSize, currentSize);
    }
}
