package client;

import client.ui.UIController;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

public class Main {
    /**
     * Start execution of program
     *
     * @param args do not use
     */
    public static void main(String[] args) {
        try {
            AppController appController = new AppController();
            appController.startWork();
        } catch (MissingArgumentException | ConfigFileNotFoundException | ConnectionException e) {
            UIController.showErrorDialog(e.getMessage());
        }
    }
}
