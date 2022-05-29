package client.ui;

import exceptions.ConnectionException;

import javax.swing.*;
import java.awt.*;

public class AuthWindow extends AbstractWindow {
    private boolean isLoginPanel = true;
    private JButton switchAuthButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JButton authButton;

    public AuthWindow(UIController uiController) {
        super("auth", uiController);
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
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel switchPanel = new JPanel();
        mainPanel.add(switchPanel);

        switchPanel.add(switchLanguageBox);

        switchAuthButton = new JButton(getString((
                isLoginPanel ? "to_register_change_button_name" : "to_login_change_button_name")));
        switchPanel.add(switchAuthButton);

        JPanel loginPanel = new JPanel(new FlowLayout());
        mainPanel.add(loginPanel);

        JLabel loginLabel = new JLabel();
        loginLabel.setText(getString("login_label"));
        loginPanel.add(loginLabel);

        loginField = new JTextField(10);
        loginPanel.add(loginField);

        JPanel passwordPanel = new JPanel();
        mainPanel.add(passwordPanel);


        JLabel passwordLabel = new JLabel(getString("password_label"));
        passwordPanel.add(passwordLabel);

        passwordField = new JPasswordField(10);
        passwordPanel.add(passwordField);

        JPanel repeatPasswordPanel = new JPanel();
        mainPanel.add(repeatPasswordPanel);

        repeatPasswordField = new JPasswordField(10);
        if (!isLoginPanel) {
            JLabel repeatPasswordLabel = new JLabel(getString("repeat_password_label"));
            repeatPasswordPanel.add(repeatPasswordLabel);
            repeatPasswordPanel.add(repeatPasswordField);
        }

        JPanel buttonPanel = new JPanel();
        mainPanel.add(buttonPanel);

        authButton = new JButton(
                getString(isLoginPanel ? "login_button_name" : "register_button_name"));
        buttonPanel.add(authButton);

        mainFrame.add(mainPanel);
    }

    @Override
    protected void setListeners() {
        switchAuthButton.addActionListener(e -> {
            isLoginPanel = !isLoginPanel;
            recreateFrame();
        });
        authButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (!isLoginPanel) {
                String repeatedPassword = new String(repeatPasswordField.getPassword());
                if (!password.equals(repeatedPassword)) {
                    UIController.showErrorDialog("passwords_not_equal");
                    return;
                }
            }
            String login = loginField.getText();
            Thread connectionThread = new Thread(() -> {
                try {
                    if (uiController.getAppController().connect(loginField.getText(),
                            new String(passwordField.getPassword()), isLoginPanel)) {
                        mainFrame.dispose();
                        uiController.createMainWindow(login);
                    }
                } catch (ConnectionException ex) {
                    UIController.showErrorDialog(ex.getMessage());
                }
            });
            connectionThread.setDaemon(true);
            connectionThread.start();
        });
    }
}
