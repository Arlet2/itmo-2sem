package client.ui.custom_graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class GraphicMap extends JPanel {
    public static final float ZOOM_COEFFICIENT = 0.1f;
    public static final float MOTION_COEFFICIENT = 1f;
    private float zoom = 1;
    private int mapX = 0;
    private int mapY = 0;
    public GraphicMap(Component parent) {
        super(null);
        setBounds(mapX, mapY, parent.getWidth(), parent.getHeight());
        setListeners();
        setBorder(BorderFactory.createBevelBorder(1));
    }

    private void updateComponents() {
        for (Component component: getComponents()) {
            if (component.getClass() == CityPainting.class)
                ((CityPainting) component).resize();
        }
        repaint();
    }
    private void setListeners() {
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                if (e.getWheelRotation() == -1) {
                    System.out.println("УВЕЛИЧИВАЮ"); // -1
                    doZoom();
                }
                else if (e.getWheelRotation() == 1) {
                    System.out.println("УМЕНЬШАЮ"); // 1
                    undoZoom();
                }
                System.out.println(zoom);
                updateComponents();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            int prevX=0;
            int prevY=0;
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (prevX > e.getX()) { // LEFT
                    System.out.print("LEFT ");
                    mapX-=MOTION_COEFFICIENT;
                }
                else if (prevX < e.getX()) { // RIGHT
                    System.out.print("RIGHT ");
                    mapX+=MOTION_COEFFICIENT;
                }
                else
                    System.out.print("DON'T X MOVE ");
                if (prevY >e.getY()) {// UP
                    System.out.println("UP");
                    mapY-=MOTION_COEFFICIENT;
                }
                else if(prevY < e.getY()) { // DOWN
                    System.out.println("DOWN");
                    mapY+=MOTION_COEFFICIENT;
                }
                else
                    System.out.println("DON'T Y MOVE");
                prevX = e.getX();
                prevY = e.getY();
                System.out.println("MAP: "+mapX+" "+mapY);
                updateComponents();
            }
        });
    }
    public void doZoom() {
        zoom+= ZOOM_COEFFICIENT;
    }
    
    public void undoZoom() {
        zoom-= ZOOM_COEFFICIENT;
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
}
