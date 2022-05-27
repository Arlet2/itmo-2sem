package client.ui;

import client.AppController;
import data_classes.City;

import javax.swing.*;
import java.util.Collection;
import java.util.ResourceBundle;

public class UIController {
    private final AppController appController;
    private MainWindow mainWindow;

    public UIController(AppController appController) {
        this.appController = appController;
    }

    public void createAuthWindow() {
        AuthWindow authWindow = new AuthWindow(this);
        authWindow.createFrame();
    }

    public void createMainWindow(String login) {
        mainWindow = new MainWindow(this, login);
        mainWindow.createFrame();
    }

    public static void showErrorDialog(String messageKey) {
        JOptionPane.showMessageDialog(null, getString("errors", messageKey),
                getString("errors", "error_name_dialog"), JOptionPane.ERROR_MESSAGE);
    }
    public static void showCustomErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg,
                getString("errors", "error_name_dialog"), JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoDialog(String messageKey) {
        JOptionPane.showMessageDialog(null, getString("servers_replies", messageKey),
                getString("servers_replies", "reply_name_dialog"), JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showInfoDialog(String message, String windowName) {
        JOptionPane.showMessageDialog(null, message, windowName, JOptionPane.INFORMATION_MESSAGE);
    }
    public static String showInputDialog(String message, String windowName) {
        return JOptionPane.showInputDialog(null, message, windowName, JOptionPane.QUESTION_MESSAGE);
    }


    public void updateData(Collection<City> cities) {
        if (mainWindow != null)
            mainWindow.refreshCitiesData(cities);
    }

    public static String getString(String resourceName, String key) {
        return ResourceBundle.getBundle(resourceName).getString(key);
    }

    public AppController getAppController() {
        return appController;
    }
}
