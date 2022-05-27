package client.ui;

import client.ui.custom_graphics.CityPainting;
import connect_utils.CommandInfo;
import connect_utils.Serializer;
import data_classes.City;
import data_classes.Climate;
import exceptions.EmptyValueException;
import exceptions.IncorrectValueException;
import exceptions.NullValueException;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;

public class MainWindow extends AbstractWindow {
    private ArrayList<City> cities = new ArrayList<>();
    private TableRowSorter<TableModel> tableSorter;
    private JTextField climateFilterField;
    private JTextField filterField;
    private JComboBox<String> filterSwitcher;
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField coordinateXField = new JTextField();
    private final JTextField coordinateYField = new JTextField();
    private final JTextField areaField = new JTextField();
    private final JTextField populationField = new JTextField();
    private final JTextField metersAboveSeaLevelField = new JTextField();
    private final JTextField establishmentDateField = new JTextField();
    private final JTextField climateField = new JTextField();
    private final JTextField governmentField = new JTextField();
    private final JTextField governorAgeField = new JTextField();
    private final JTextField governorBirthdayField = new JTextField();

    private JButton insertButton;
    private JButton updateButton;
    private JButton removeKeyButton;
    private JButton removeLowerKeyButton;
    private JButton replaceIfGreaterButton;
    private JButton clearButton;
    private JButton executeScriptButton;
    private JButton historyButton;

    private final JTable table = new JTable();

    public final String login;

    public MainWindow(UIController uiController, String login) {
        super("main", uiController);
        this.login = login;
    }

    @Override
    protected void createCustomFrame() {
        int sizeWidth = 5 * screenSize.width / 6;
        int sizeHeight = 5 * screenSize.height / 6;
        mainFrame = new JFrame(getString("window_name"));
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                uiController.getAppController().exit();
            }
        });
        mainFrame.setBounds((screenSize.width - sizeWidth) / 2, (screenSize.height - sizeHeight) / 2,
                sizeWidth, sizeHeight);
        mainFrame.setMinimumSize(new Dimension(sizeWidth, sizeHeight));
        JPanel mainPanel = new JPanel(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        tabs.addTab(getString("table_tab_name"), createTableTab());
        tabs.addTab(getString("map_tab_name"), createMapTab());
        tabs.setSelectedIndex(1);
        mainPanel.add(tabs);
        mainFrame.add(mainPanel);
    }

    @Override
    protected void setListeners() {
        climateFilterField.addActionListener(e -> {
            updateClimateFilter(climateFilterField.getText());
        });
        filterField.addActionListener(e -> {
            int column = -1;
            if (filterSwitcher.getSelectedItem() != null) {
                for (Headers header : Headers.values()) {
                    if (filterSwitcher.getSelectedItem().equals(getString(header.key))) {
                        column = header.ordinal();
                        break;
                    }
                }
            }
            if (column == -1)
                return;

            updateFilter(column, filterField.getText());
        });
        insertButton.addActionListener(e -> {
            insertAction();
        });
        updateButton.addActionListener(e -> {
            updateAction();
        });
        removeKeyButton.addActionListener(e -> {
            removeKeyAction();
        });
        removeLowerKeyButton.addActionListener(e -> {
            removeLowerKeyAction();
        });
        replaceIfGreaterButton.addActionListener(e -> {
            replaceIfGreaterAction();
        });
        clearButton.addActionListener(e -> {
            clearAction();
        });
        historyButton.addActionListener(e -> {
            historyAction();
        });
        executeScriptButton.addActionListener(e -> {
            String path = UIController.showInputDialog(getString("execute_script_input"),
                    getString("execute_script_window_name"));
            if (path == null || path.equals("")) {
                UIController.showErrorDialog("incorrect_path");
            }
            ArrayList<String> commands;
            try {
                commands = uiController.getAppController().getFileController().readScriptFile(path);
            } catch (FileNotFoundException ex) {
                UIController.showErrorDialog("script_file_not_found");
                return;
            }
            ArrayList<String> unknownCommands = new ArrayList<>();
            for (String s : commands) {
                switch (s) {
                    case "insert":
                        insertAction();
                        break;
                    case "update":
                        updateAction();
                        break;
                    case "remove_key":
                        removeKeyAction();
                        break;
                    case "remove_lower_key":
                        removeLowerKeyAction();
                        break;
                    case "replace_if_greater":
                        replaceIfGreaterAction();
                        break;
                    case "clear":
                        clearAction();
                        break;
                    case "history":
                        historyAction();
                        break;
                    default:
                        unknownCommands.add(s);
                        break;
                }
            }
            if (!unknownCommands.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < unknownCommands.size(); i++)
                    stringBuilder.append(i + 1).append(") ").append(unknownCommands.get(i)).append("\n");
                UIController.showCustomErrorDialog(getString("unknown_commands", "errors") + "\n" + stringBuilder);
            } else
                UIController.showInfoDialog("execute_script_success");
            uiController.getAppController().addCommandToHistory(new CommandInfo("execute_script"));
        });
    }

    private void historyAction() {
        StringBuilder msg = new StringBuilder();
        if (uiController.getAppController().getHistory().isEmpty())
            UIController.showInfoDialog(getString("history_is_empty"),
                    getString("history_window_name"));
        else {
            for (int i = 0; i < uiController.getAppController().getHistory().size(); i++)
                msg.append(i + 1).append(") ")
                        .append(getString(uiController.getAppController().getHistory().get(i)
                                .getName() + "_button_name")).append("\n");
            UIController.showInfoDialog(msg.toString(), getString("history_window_name"));
        }
        uiController.getAppController().addCommandToHistory(new CommandInfo("history"));
    }

    private void insertAction() {
        try {
            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("insert", Serializer.convertObjectToBytes(cityCreator())));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        } catch (IncorrectValueException | NullValueException | DateTimeParseException | EmptyValueException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog(ex.getMessage());
        }
    }

    private void updateAction() {
        try {
            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("update", Serializer.convertObjectToBytes(cityCreator())));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        } catch (IncorrectValueException | NullValueException | DateTimeParseException | EmptyValueException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog(ex.getMessage());
        }
    }

    private void removeKeyAction() {
        try {
            long id;
            try {
                id = Long.parseLong(idField.getText().replaceAll("[,.\\s]", ""));
                if (id <= 0) {
                    UIController.showErrorDialog("id_is_greater_zero");
                    return;
                }
            } catch (NumberFormatException ex) {
                UIController.showErrorDialog("id_is_integer");
                return;
            }

            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("remove_key", Serializer.convertObjectToBytes(
                            new String[]{"", id + ""}
                    )));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        }
    }

    private void removeLowerKeyAction() {
        try {
            long id;
            try {
                id = Long.parseLong(idField.getText().replaceAll("[,.\\s]", ""));
                if (id <= 0) {
                    UIController.showErrorDialog("id_is_greater_zero");
                    return;
                }
            } catch (NumberFormatException ex) {
                UIController.showErrorDialog("id_is_integer");
                return;
            }
            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("remove_lower_key", Serializer.convertObjectToBytes(
                            new String[]{"", id + ""}
                    )));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        }
    }

    private void replaceIfGreaterAction() {
        try {
            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("replace_if_greater", Serializer.convertObjectToBytes(cityCreator())));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        } catch (IncorrectValueException | NullValueException | DateTimeParseException | EmptyValueException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog(ex.getMessage());
        }
    }

    private void clearAction() {
        try {
            uiController.getAppController().getConnectionController().getRequestController()
                    .sendCommand(new CommandInfo("clear", Serializer.convertObjectToBytes("")));
        } catch (IOException | NotYetConnectedException ex) {
            ex.printStackTrace();
            UIController.showErrorDialog("server_is_unavailable");
        }
    }

    private City cityCreator() throws IncorrectValueException, NullValueException,
            EmptyValueException {
        City city = new City();
        try {
            city.setId(Long.parseLong(idField.getText().replaceAll("[\\s.,]", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("id_is_integer");
        }
        city.setName(nameField.getText());
        try {
            city.getCoordinates().setX(Float.parseFloat(coordinateXField.getText()
                    .replaceAll("\\s", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("x_is_float");
        }
        try {
            city.getCoordinates().setY(Integer.parseInt(coordinateYField.getText()
                    .replaceAll("\\s,.", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("y_is_integer");
        }
        city.setCreationDate(ZonedDateTime.now());
        try {
            city.setArea(Long.parseLong(areaField.getText().replaceAll("[\\s.,]", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("area_is_integer");
        }
        try {
            city.setPopulation(Integer.parseInt(populationField.getText()
                    .replaceAll("\\s,.", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("population_is_integer");
        }
        try {
            city.setMetersAboveSeaLevel(Long.parseLong(metersAboveSeaLevelField.getText()
                    .replaceAll("[\\s.,]", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("meters_above_sea_level_integer");
        }
        try {
            city.setEstablishmentDate(LocalDate.parse(establishmentDateField.getText(),
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                            .withLocale(Locale.getDefault())));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IncorrectValueException("incorrect_establishment_date");
        }
        city.setClimate(climateField.getText().toUpperCase());
        city.setGovernment(governmentField.getText().toUpperCase());
        try {
            city.getGovernor().setAge(Long.parseLong(governorAgeField.getText().replaceAll("[\\s.,]", "")));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("age_is_integer");
        }
        try {
            city.getGovernor().setBirthday(LocalDateTime.parse(
                    governorBirthdayField.getText(),
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                            .withLocale(Locale.getDefault())));
        } catch (DateTimeParseException e) {
            throw new IncorrectValueException("incorrect_birthday");
        }
        return city;
    }

    private JPanel createMapTab() {
        updateMapData();
        /*
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMinimumSize(mainFrame.getMinimumSize());
        int sh = 0;
        for (City city : cities) {
            CityPainting cityPainting = new CityPainting(city);
            cityPainting.setBounds((int) city.getCoordinates().getX(), city.getCoordinates().getY(),
                    (int) city.getArea() * 100, (int) city.getArea() * 100);
            cityPainting.setMaximumSize(new Dimension((int) city.getArea(), (int) city.getArea()));
            cityPainting.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("LOX " + city.getId());
                }
            });
            mainPanel.add(cityPainting);
            sh++;
        }
         */
        return null;
    }

    private void updateMapData() {

    }

    private JPanel createTableTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMinimumSize(mainFrame.getSize());
        updateTableData();
        JScrollPane scrollTablePane = new JScrollPane(table);
        mainPanel.add(scrollTablePane);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        mainPanel.add(rightPanel, "East");

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolPanel);

        JLabel currentUserLabel = new JLabel(getString("current_user_label") + ": " + login);
        toolPanel.add(currentUserLabel);

        toolPanel.add(switchLanguageBox);

        JPanel filterClimatePanel = new JPanel();

        JLabel climateFilterLabel = new JLabel(getString("climate_filter_label_name"));
        filterClimatePanel.add(climateFilterLabel);

        climateFilterField = new JTextField(10);
        filterClimatePanel.add(climateFilterField);

        rightPanel.add(filterClimatePanel);

        JPanel filterPanel = new JPanel();

        JLabel allFilterLabel = new JLabel(getString("filter_label_name"));
        filterPanel.add(allFilterLabel);

        filterSwitcher = new JComboBox<>();
        for (Headers header : Headers.values())
            filterSwitcher.addItem(getString(header.key));
        filterPanel.add(filterSwitcher);

        filterField = new JTextField(10);
        filterPanel.add(filterField);

        rightPanel.add(filterPanel);

        JPanel buttonPanel = new JPanel();
        rightPanel.add(buttonPanel);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        executeScriptButton = new JButton(getString("execute_script_button_name"));
        buttonPanel.add(executeScriptButton);

        historyButton = new JButton(getString("history_button_name"));
        buttonPanel.add(historyButton);

        JPanel leftPanel = new JPanel();
        mainPanel.add(leftPanel, "West");
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel creatorPanel = new JPanel();
        leftPanel.add(creatorPanel);
        creatorPanel.setLayout(new BoxLayout(creatorPanel, BoxLayout.Y_AXIS));

        creatorPanel.add(createFieldPanel(Headers.ID.key, idField));
        creatorPanel.add(createFieldPanel(Headers.NAME.key, nameField));
        creatorPanel.add(createFieldPanel(Headers.COORDINATE_X.key, coordinateXField));
        creatorPanel.add(createFieldPanel(Headers.COORDINATE_Y.key, coordinateYField));
        creatorPanel.add(createFieldPanel(Headers.AREA.key, areaField));
        creatorPanel.add(createFieldPanel(Headers.POPULATION.key, populationField));
        creatorPanel.add(createFieldPanel(Headers.METERS_ABOVE_SEA_LEVEL.key, metersAboveSeaLevelField));
        creatorPanel.add(createFieldPanel(Headers.ESTABLISHMENT_DATE.key, establishmentDateField));
        creatorPanel.add(createFieldPanel(Headers.CLIMATE.key, climateField));
        creatorPanel.add(createFieldPanel(Headers.GOVERNMENT.key, governmentField));
        creatorPanel.add(createFieldPanel(Headers.GOVERNOR_AGE.key, governorAgeField));
        creatorPanel.add(createFieldPanel(Headers.GOVERNOR_BIRTHDAY.key, governorBirthdayField));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        leftPanel.add(buttonsPanel);

        insertButton = new JButton(getString("insert_button_name"));
        updateButton = new JButton(getString("update_button_name"));
        removeKeyButton = new JButton(getString("remove_key_button_name"));
        removeLowerKeyButton = new JButton(getString("remove_lower_key_button_name"));
        replaceIfGreaterButton = new JButton(getString("replace_if_greater_button_name"));
        clearButton = new JButton(getString("clear_button_name"));

        leftPanel.add(insertButton);
        leftPanel.add(updateButton);
        leftPanel.add(removeKeyButton);
        leftPanel.add(removeLowerKeyButton);
        leftPanel.add(replaceIfGreaterButton);
        leftPanel.add(clearButton);

        return mainPanel;
    }

    private JPanel createFieldPanel(String fieldName, JTextField usingField) {
        fieldName = getString(fieldName);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(fieldName);
        panel.add(label);

        panel.add(usingField);

        return panel;
    }

    public void refreshCitiesData(Collection<City> newCities) {
        cities = new ArrayList<>(newCities);
        updateTableData();
        updateMapData();
    }

    private void updateTableData() {
        Object[][] values = new Object[cities.size()][14];
        for (int i = 0; i < cities.size(); i++) {
            values[i][0] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getId());
            values[i][1] = cities.get(i).getName();
            values[i][2] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getCoordinates().getX());
            values[i][3] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getCoordinates().getY());
            values[i][4] = cities.get(i).getCreationDate()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.getDefault()));
            values[i][5] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getArea());
            values[i][6] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getPopulation());
            values[i][7] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getMetersAboveSeaLevel());
            values[i][8] = cities.get(i).getEstablishmentDate().format(DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()));
            values[i][9] = cities.get(i).getClimateString();
            values[i][10] = cities.get(i).getGovernmentString();
            values[i][11] = NumberFormat.getInstance(Locale.getDefault())
                    .format(cities.get(i).getGovernor().getAge().longValue());
            values[i][12] = cities.get(i).getGovernor().getBirthday()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault()));
            values[i][13] = cities.get(i).getOwner();
        }
        Object[] headers = new Object[Headers.values().length];
        for (Headers header : Headers.values()) {
            headers[header.ordinal()] = getString(header.key);
        }
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
                super.setDataVector(dataVector, columnIdentifiers);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        model.setDataVector(values, headers);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(x -> {
            if (x.getValueIsAdjusting()) {
                int rowIndex = table.getSelectedRow();
                if (rowIndex == -1)
                    return;
                ;
                idField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 0));
                nameField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 1));
                coordinateXField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 2));
                coordinateYField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 3));
                areaField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 5));
                populationField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 6));
                metersAboveSeaLevelField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 7));
                establishmentDateField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 8));
                climateField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 9));
                governmentField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 10));
                governorAgeField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 11));
                governorBirthdayField.setText((String) table.getModel()
                        .getValueAt(table.getRowSorter().convertRowIndexToModel(rowIndex), 12));
            }
        });
        table.setAutoCreateRowSorter(true);

        tableSorter = createRowSorter(table.getModel());
        table.setRowSorter(tableSorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.setRowSelectionAllowed(true);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void updateFilter(int columnIndex, String value) {
        tableSorter.setRowFilter(RowFilter.regexFilter(value, columnIndex));
    }

    private void updateClimateFilter(String climate) {
        Climate currentClimate;
        try {
            currentClimate = Climate.valueOf(climate.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (!climate.equals("")) {
                UIController.showErrorDialog("not_valid_climate");
            }
            currentClimate = null;
        }
        Climate finalCurrentClimate = currentClimate;
        tableSorter.setRowFilter(
                new RowFilter<TableModel, Object>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ?> entry) {
                        if (finalCurrentClimate == null)
                            return true;
                        return finalCurrentClimate.ordinal() <
                                Climate.valueOf((String) entry.getValue(9)).ordinal();
                    }
                }
        );
    }

    private TableRowSorter<TableModel> createRowSorter(TableModel tableModel) {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableModel);

        rowSorter.setComparator(0, Comparator.comparingLong(o -> (Long.parseLong(((String) o)
                .replaceAll("[,.\\s]", "")))));
        rowSorter.setComparator(2, Comparator.comparingDouble(o -> (Float.parseFloat(((String) o)
                .replaceAll("\\s", "")))));
        rowSorter.setComparator(3, Comparator.comparingInt(o -> (Integer.parseInt(((String) o)
                .replaceAll("\\s,.", "")))));
        rowSorter.setComparator(5, Comparator.comparingLong(o -> (Long.parseLong(((String) o)
                .replaceAll("\\s,.", "")))));
        rowSorter.setComparator(6, Comparator.comparingInt(o -> (Integer.parseInt(((String) o)
                .replaceAll("\\s,.", "")))));
        rowSorter.setComparator(7, Comparator.comparingLong(o -> (Long.parseLong(((String) o)
                .replaceAll("\\s,.", "")))));
        rowSorter.setComparator(11, Comparator.comparingLong(o -> (Long.parseLong(((String) o)
                .replaceAll("\\s,.", "")))));

        return rowSorter;
    }

    enum Headers {
        ID("id_column_name"),
        NAME("name_column_name"),
        COORDINATE_X("coordinate_x_column_name"),
        COORDINATE_Y("coordinate_y_column_name"),
        CREATION_DATE("creation_date_column_name"),
        AREA("area_column_name"),
        POPULATION("population_column_name"),
        METERS_ABOVE_SEA_LEVEL("meters_above_sea_level_column_name"),
        ESTABLISHMENT_DATE("establishment_date_column_name"),
        CLIMATE("climate_column_name"),
        GOVERNMENT("government_column_name"),
        GOVERNOR_AGE("governor_age_name"),
        GOVERNOR_BIRTHDAY("governor_birthday_name"),
        OWNER("owner_column_name");

        final String key;

        Headers(String key) {
            this.key = key;
        }
    }
}
