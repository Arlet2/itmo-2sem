package client.ui;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int frameWidth = screenSize.width/8;
    private int frameHeight = screenSize.height/5;
    public ErrorDialog() {
        super();
        setName("error");
        setBounds((screenSize.width-frameWidth)/2, (screenSize.height-frameHeight)/2, frameWidth, frameHeight);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
