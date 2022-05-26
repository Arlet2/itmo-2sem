package client;

import client.ui.AuthWindow;
import client.ui.UIController;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

import java.util.Locale;

public class Main {
    /**
     * Start execution of program
     *
     * @param args do not use
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        try {
            AppController appController = new AppController();
            //appController.startWork();
            appController.getUiController().createMainWindow("test");
        } catch (MissingArgumentException | ConfigFileNotFoundException | ConnectionException  e) {
            UIController.showErrorDialog(e.getMessage());
        }
        //MainWindowUI window = new MainWindowUI();
        //window.createFrame();
        //window.changeLocale(null);
        /*
        CommandController cc;
        try {
            cc = new CommandController();
        } catch (MissingArgumentException e) {
            System.out.println("Ошибка в файле конфигурации: " + e.getMessage());
            return;
        } catch (ConfigFileNotFoundException | ConnectionException e) {
            System.out.println(e.getMessage());
            return;
        }
        try {
            cc.connect();
        } catch (ConnectionException e) {
            System.out.println(e.getMessage());
        }
         */
    }
}
