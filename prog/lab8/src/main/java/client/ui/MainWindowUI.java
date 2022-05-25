package client.ui;

import data_classes.City;
import data_classes.Climate;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

public class MainWindowUI extends AbstractWindow {
    private ArrayList<City> cities;
    private TableRowSorter<TableModel> tableSorter;
    private JTextField climateFilterField;
    private JTextField filterField;
    private JComboBox<String> filterSwitcher;
    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(10);
    private final JTextField coordinateXField = new JTextField(10);
    private final JTextField coordinateYField = new JTextField(10);
    private final JTextField areaField = new JTextField(10);
    private final JTextField populationField = new JTextField(10);
    private final JTextField metersAboveSeaLevelField = new JTextField(10);
    private final JTextField establishmentDateField = new JTextField(10);
    private final JTextField climateField = new JTextField(10);
    private final JTextField governmentField = new JTextField(10);
    private final JTextField governorAgeField = new JTextField(10);
    private final JTextField governorBirthdayField = new JTextField(10);

    private JButton insertButton;
    private JButton updateButton;
    private JButton removeKeyButton;
    private JButton removeLowerKeyButton;
    private JButton replaceIfGreaterButton;
    private JButton clearButton;
    private JButton executeScriptButton;
    private JButton historyButton;

    private JTable table;

    public MainWindowUI() {
        super("main");
    }

    @Override
    protected void createCustomFrame() {
        int sizeWidth = 5 * screenSize.width / 6;
        int sizeHeight = 5 * screenSize.height / 6;
        mainFrame = new JFrame(getString("window_name"));
        mainFrame.setBounds((screenSize.width - sizeWidth) / 2, (screenSize.height - sizeHeight) / 2,
                sizeWidth, sizeHeight);
        mainFrame.setMinimumSize(new Dimension(sizeWidth, sizeHeight));
        JPanel mainPanel = new JPanel(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        tabs.addTab(getString("table_tab_name"), createTableTab());
        tabs.addTab(getString("map_tab_name"), createMapTab());
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
                    if (((String) filterSwitcher.getSelectedItem()).equals(getString(header.key))) {
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

        });
        updateButton.addActionListener(e -> {

        });
        removeKeyButton.addActionListener(e -> {

        });
        removeLowerKeyButton.addActionListener(e -> {

        });
        replaceIfGreaterButton.addActionListener(e -> {

        });
        clearButton.addActionListener(e -> {

        });
    }


    private JPanel createMapTab() {
        readCityCollection();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMinimumSize(mainFrame.getMinimumSize());
        int sh = 0;
        for (City city: cities) {
            CityPainting cityPainting = new CityPainting(city);
            cityPainting.setBounds((int)city.getCoordinates().getX(), city.getCoordinates().getY(),
                    (int)city.getArea()*100, (int)city.getArea()*100);
            cityPainting.setMaximumSize(new Dimension((int)city.getArea(), (int)city.getArea()));
            cityPainting.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("LOX "+city.getId());
                }
            });
            mainPanel.add(cityPainting);
            sh++;
        }
        return mainPanel;
    }

    private JPanel createTableTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setMinimumSize(mainFrame.getSize());
        JScrollPane scrollTablePane = new JScrollPane(createTable());
        mainPanel.add(scrollTablePane);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        mainPanel.add(rightPanel, "East");

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolPanel);

        JLabel currentUserLabel = new JLabel(getString("current_user_label")+": "+ "sad");
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
        for(Headers header : Headers.values())
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
        creatorPanel.add(createFieldPanel(Headers.GOVERNOR_BIRTHDAY.key,  governorBirthdayField));

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

    private JTable createTable() {
        readCityCollection();
        Object[][] values = new Object[cities.size()][13];
        for (int i = 0; i < cities.size(); i++) {
            values[i][0] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getId());
            values[i][1] = cities.get(i).getName();
            values[i][2] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getCoordinates().getX());
            values[i][3] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getCoordinates().getY());
            values[i][4] = cities.get(i).getCreationDate()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(getCurrentLocale()));
            values[i][5] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getArea());
            values[i][6] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getPopulation());
            values[i][7] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getMetersAboveSeaLevel());
            values[i][8] = cities.get(i).getEstablishmentDate().format(DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getCurrentLocale()));
            values[i][9] = cities.get(i).getClimateString();
            values[i][10] = cities.get(i).getGovernmentString();
            values[i][11] = NumberFormat.getInstance(getCurrentLocale())
                    .format(cities.get(i).getGovernor().getAge().longValue());
            values[i][12] = cities.get(i).getGovernor().getBirthday()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getCurrentLocale()));
        }
        Object[] headers = new Object[Headers.values().length];
        for(Headers header : Headers.values()) {
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
        model.setDataVector(values, headers);
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        tableSorter = createRowSorter(table.getModel());
        table.setRowSorter(tableSorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.getTableHeader().setReorderingAllowed(false);
        table.setRowSelectionAllowed(true);
        table.getSelectionModel().addListSelectionListener(x -> {
            if (x.getValueIsAdjusting()) {
                int rowIndex = table.getSelectedRow();
                idField.setText((String)table.getModel().getValueAt(rowIndex, 0));
                nameField.setText((String)table.getModel().getValueAt(rowIndex, 1));
                coordinateXField.setText((String)table.getModel().getValueAt(rowIndex, 2));
                coordinateYField.setText((String)table.getModel().getValueAt(rowIndex, 3));
                areaField.setText((String)table.getModel().getValueAt(rowIndex, 5));
                populationField.setText((String)table.getModel().getValueAt(rowIndex, 6));
                metersAboveSeaLevelField.setText((String)table.getModel().getValueAt(rowIndex, 7));
                establishmentDateField.setText((String)table.getModel().getValueAt(rowIndex, 8));
                climateField.setText((String)table.getModel().getValueAt(rowIndex,9));
                governmentField.setText((String)table.getModel().getValueAt(rowIndex, 10));
                governorAgeField.setText((String)table.getModel().getValueAt(rowIndex, 11));
                governorBirthdayField.setText((String)table.getModel().getValueAt(rowIndex, 12));
            }
        });
        return table;
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
                JOptionPane.showMessageDialog(mainFrame, getString("not_valid_climate", "errors"),
                        getString("error_name_dialog", "errors"), JOptionPane.ERROR_MESSAGE);
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

        rowSorter.setComparator(0, Comparator.comparingLong(o -> (Long.parseLong((String) o))));
        rowSorter.setComparator(2, Comparator.comparingDouble(o -> (Float.parseFloat((String) o))));
        rowSorter.setComparator(3, Comparator.comparingInt(o -> (Integer.parseInt((String) o))));
        rowSorter.setComparator(5, Comparator.comparingLong(o -> (Long.parseLong((String) o))));
        rowSorter.setComparator(6, Comparator.comparingInt(o -> Integer.parseInt((String) o)));
        rowSorter.setComparator(7, Comparator.comparingLong(o -> Long.parseLong((String) o)));
        rowSorter.setComparator(11, Comparator.comparingLong(o -> Long.parseLong((String) o)));

        return rowSorter;
    }

    private void readCityCollection() {
        cities = new ArrayList<>();
        City city;
        for (int i = 0; i < 5; i++) {
            city = new City();
            city.setId(i + 1L);
            city.setName("abc");
            if (i == 0)
                city.getCoordinates().setX(50);
            else if (i==1)
                city.getCoordinates().setX(45);
            else if (i==2)
                city.getCoordinates().setX(23);
            city.getCoordinates().setY(2);
            city.setCreationDate(ZonedDateTime.now());
            city.setArea(i + 9L);
            city.setPopulation(4);
            city.setClimate(Climate.values()[Math.abs(2 - i)]);
            city.setMetersAboveSeaLevel(2L);
            city.setEstablishmentDate(LocalDate.now());
            city.getGovernor().setAge(1L);
            city.getGovernor().setBirthday(LocalDateTime.now());
            cities.add(city);
            System.out.println(city.getId());
        }
        cities.get(0).setName("abcd");
        cities.get(1).setName("A");
        cities.get(3).setName("ab");
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
