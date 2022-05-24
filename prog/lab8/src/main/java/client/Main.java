package client;

import client.ui.AuthUI;
import client.ui.MainWindowUI;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    /**
     * Start execution of program
     *
     * @param args do not use
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        MainWindowUI window = new MainWindowUI();
        window.createFrame();
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
