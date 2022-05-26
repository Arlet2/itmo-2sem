package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractWindow {
    private ResourceBundle resourceBundle;
    protected Dimension screenSize;
    protected JFrame mainFrame;
    protected JComboBox<String> switchLanguageBox;
    protected Languages language;
    private final String resourcesName;
    protected final UIController uiController;

    public AbstractWindow(String resourcesName, UIController uiController) {
        this.uiController = uiController;
        this.resourcesName = resourcesName;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        resourceBundle = ResourceBundle.getBundle(resourcesName, Locale.getDefault());
        language = getLanguage(Locale.getDefault());
    }

    public void changeLocale(Locale newLocale) {
        if (newLocale == null || newLocale.getLanguage().equals("en")) {
            resourceBundle = ResourceBundle.getBundle(resourcesName, Locale.ROOT);
            language = Languages.EN;
            Locale.setDefault(language.getLocale());
        }
        else {
            resourceBundle = ResourceBundle.getBundle(resourcesName, newLocale);
            language = getLanguage(newLocale);
            Locale.setDefault(language.getLocale());
        }
        recreateFrame();
    }
    protected void recreateFrame() {
        mainFrame.dispose();
        createFrame();
    }
    protected abstract void createCustomFrame();
    protected abstract void setListeners();

    public void createFrame() {
        switchLanguageBox = new JComboBox<>();
        for(Languages language: Languages.values())
            switchLanguageBox.addItem(getString(language.getName(), "languages_name"));
        switchLanguageBox.setSelectedItem(getString(language.getName(), "languages_name"));
        switchLanguageBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                changeLocale(getLanguage((String) e.getItem()).getLocale());
        });

        createCustomFrame();
        setListeners();

        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.pack();
    }

    protected Languages getLanguage (Locale locale) {
        for(Languages ilanguage: Languages.values()) {
            if(ilanguage.getLocale() == locale)
                return ilanguage;
        }
        for(Languages ilanguage: Languages.values()) {
            if(ilanguage.getLocale().getLanguage().equals(locale.getLanguage()))
                return ilanguage;
        }
        return Languages.EN;
    }
    protected Languages getLanguage(String name) {
        for(Languages ilanguage: Languages.values()) {
            if((getString(ilanguage.getName(), "languages_name")).equals(name))
                return ilanguage;
        }
        return Languages.EN;
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }
    public String getString(String key, String resourcesName) {
        return ResourceBundle.getBundle(resourcesName).getString(key);
    }

    public UIController getUiController() {
        return uiController;
    }

    public enum Languages {
        RU(new Locale("ru"), "ru_name"),
        DA(new Locale("da"), "da_name"),
        EN(new Locale("en"), "en_name"),
        ES_CR(new Locale("es", "CR"), "es_cr_name"),
        SR(new Locale("sr"), "sr_name");
        private final Locale locale;
        private final String name;

        Languages(Locale locale, String name) {
            this.locale = locale;
            this.name = name;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getName() {
            return name;
        }
    }
}
