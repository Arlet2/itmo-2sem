package client.ui.custom_graphics;

import client.ui.UIController;
import data_classes.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class CityPainting extends JPanel {
    public static final int FRAME_COUNTER = 50;
    private final int x;
    private final int y;
    private final long id;
    private final Color color;
    private final int size;
    private int currentSize;
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
        setListeners();
        if (!isNew) {
            currentSize = size;
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            move();
        }
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
                    String name = (String) map.getWindow().getTable().getModel().getValueAt(rowIndex, 1);
                    String coordinateX = (String) map.getWindow().getTable().getModel().getValueAt(rowIndex, 2);
                    String coordinateY = (String) map.getWindow().getTable().getModel().getValueAt(rowIndex, 3);
                    String area = (String) map.getWindow().getTable().getModel().getValueAt(rowIndex, 5);
                    String population = (String) map.getWindow().getTable().getModel().getValueAt(rowIndex, 6);
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
                } else if (e.getButton() == MouseEvent.BUTTON1) {
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

    public void animation(int frameCount) {
        if (FRAME_COUNTER - 1 == frameCount) {
            currentSize = size;
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        } else
            currentSize = size / (FRAME_COUNTER - frameCount + 1);
        move(currentSize);
        map.repaint();
    }

    public void move() {
        setBounds(x + map.getMapX(), y + map.getMapY(), currentSize, currentSize);
        setSizes();
        repaint();
    }

    public void move(int size) {
        setBounds(x + map.getMapX(), y + map.getMapY(), size, size);
        setSizes();
        repaint();
    }

    private void setSizes() {
        setSizes(size);
    }

    private void setSizes(int size) {
        setMinimumSize(new Dimension(size, size));
        setSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, currentSize, currentSize);
    }
}
