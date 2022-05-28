package client.ui.custom_graphics;

import client.ui.MainWindow;
import data_classes.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GraphicMap extends JPanel {
    public static final float ZOOM_COEFFICIENT = 0.5f;
    public static final float MOTION_COEFFICIENT = 3f;
    private float zoom = 1;
    private int mapX = 0;
    private int mapY = 0;
    private int zoomCounter = 0;
    private final MainWindow window;
    private final HashMap<String, Color> ownersColors;

    public GraphicMap(MainWindow window) {
        super(null);
        this.window = window;
        ownersColors = new HashMap<>();
        setBounds(mapX, mapY, window.getFrameWidth(), window.getFrameHeight());
        setListeners();
        setBorder(BorderFactory.createBevelBorder(1));
    }

    public void addColorToCollection(String owner, Color color) {
        ownersColors.put(owner, color);
    }

    public Color getColorByOwner(String owner) {
        return ownersColors.get(owner);
    }

    public boolean isThisColorOnCollection(Color color) {
        for (Color collectionColor : ownersColors.values()) {
            if (collectionColor == color)
                return true;
        }
        return false;
    }

    public void loadCities(Collection<City> cities, Collection<Long> cityToAdd) {
        mapX = 0;
        mapY = 0;
        zoomCounter = 0;
        for (Component component : getComponents()) {
            if (component.getClass() == CityPainting.class) {
                remove(component);
            }
        }
        cities = cities.stream().sorted(Comparator.comparingLong(City::getArea))
                .collect(Collectors.toList());
        boolean isAppend;
        for (City city : cities) {
            isAppend = false;
            for (long id : cityToAdd) {
                if (id == city.getId()) {
                    add(new CityPainting(this, city, true));
                    isAppend = true;
                    break;
                }
            }
            if (!isAppend)
                add(new CityPainting(this, city, false));
        }
    }

    public void repaintComponents(boolean zoomIsUsed) {
        if (!zoomIsUsed)
            resetZoom();
        for (Component component : getComponents()) {
            if (component.getClass() == CityPainting.class)
                ((CityPainting) component).resize(false);
        }
        repaint();
    }

    private void moveComponents() {
        for (Component component : getComponents()) {
            if (component.getClass() == CityPainting.class)
                ((CityPainting) component).move(false);
        }
        repaint();
    }

    private void setListeners() {
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                if (e.getWheelRotation() == -1) {
                    doZoom();
                    zoomCounter++;
                } else if (e.getWheelRotation() == 1) {
                    undoZoom();
                    zoomCounter--;
                }
                System.out.println(zoom);
                repaintComponents(true);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            int prevX = 0;
            int prevY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (prevX > e.getX()) { // LEFT
                    mapX -= MOTION_COEFFICIENT;
                } else if (prevX < e.getX()) { // RIGHT
                    mapX += MOTION_COEFFICIENT;
                }
                if (prevY > e.getY()) {// UP
                    mapY -= MOTION_COEFFICIENT;
                } else if (prevY < e.getY()) { // DOWN
                    mapY += MOTION_COEFFICIENT;
                }
                prevX = e.getX();
                prevY = e.getY();
                moveComponents();
            }
        });
    }

    public void doZoom() {
        resetZoom();
        zoom += ZOOM_COEFFICIENT;
    }

    public void undoZoom() {
        resetZoom();
        zoom -= ZOOM_COEFFICIENT;
    }

    public float getZoom() {
        return zoom;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public void setMapX(int mapX) {
        this.mapX = mapX;
    }

    public void setMapY(int mapY) {
        this.mapY = mapY;
    }

    public void resetZoom() {
        zoom=1;
    }

    public int getZoomCounter() {
        return zoomCounter;
    }

    public MainWindow getWindow() {
        return window;
    }
}
