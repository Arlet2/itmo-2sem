package client.ui;

import javax.swing.*;
import java.awt.*;

public class AuthUI extends AbstractWindow {
    private boolean isLoginPanel = true;
    private JButton switchAuthButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton authButton;
    public AuthUI() {
        super("auth");
    }


    @Override
    protected void createCustomFrame() {
        int sizeWidth = screenSize.width / 5;
        int sizeHeight = screenSize.height / 3;
        int x = (screenSize.width - sizeWidth) / 2;
        int y = (screenSize.height - sizeHeight) / 2;

        mainFrame = new JFrame(getString("window_name"));
        mainFrame.setBounds(x, y, sizeWidth, sizeHeight);
        mainFrame.setMinimumSize(new Dimension(sizeWidth, sizeHeight));

        JPanel mainPanel = new JPanel();

        mainPanel.add(switchLanguageBox);

        switchAuthButton = new JButton(getString((
                isLoginPanel?"to_register_change_button_name":"to_login_change_button_name")));
        mainPanel.add(switchAuthButton);

        JPanel authPanel = new JPanel(new FlowLayout());

        JLabel loginLabel = new JLabel();
        loginLabel.setText(getString("login_label"));
        authPanel.add(loginLabel);

        loginField = new JTextField(10);
        authPanel.add(loginField);

        JLabel passwordLabel = new JLabel(getString("password_label"));
        authPanel.add(passwordLabel);

        passwordField = new JPasswordField(10);
        authPanel.add(passwordField);

        JPasswordField repeatPasswordField = null;
        if(!isLoginPanel) {
            JLabel repeatPasswordLabel = new JLabel(getString("repeat_password_label"));
            authPanel.add(repeatPasswordLabel);

            repeatPasswordField = new JPasswordField(10);
            authPanel.add(repeatPasswordField);
        }

        authButton = new JButton(
                getString(isLoginPanel?"login_button_name":"register_button_name"));
        authPanel.add(authButton);

        mainPanel.add(authPanel);

        mainFrame.add(mainPanel);
    }
    @Override
    protected void setListeners() {
        switchAuthButton.addActionListener(e -> {
            isLoginPanel = !isLoginPanel;
            recreateFrame();
        });
        authButton.addActionListener(e -> {
            if(isLoginPanel) {
                System.out.println("CHECK LOGIN");
            }
            else {
                System.out.println("REGISTER");
            }
            mainFrame.dispose();
        });
    }
}
