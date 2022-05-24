package client.ui;

import data_classes.City;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;

public class MainWindowUI extends AbstractWindow {
    private ArrayList<City> cities;

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
        mainFrame.setSize(new Dimension(sizeWidth, sizeHeight));
        JPanel mainPanel = new JPanel(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        tabs.addTab(getString("table_tab_name"), createTableTab());
        tabs.addTab(getString("map_tab_name"), createMapTab());


        mainPanel.add(tabs);
        mainFrame.add(mainPanel);
    }

    @Override
    protected void setListeners() {

    }

    private JPanel createMapTab() {
        JPanel mapPanel = new JPanel(new BorderLayout());
        mapPanel.add(new JLabel("MAP"));
        Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                g.drawString("Hello", 100, 100);
                setBackground(Color.WHITE);
                g.setColor(Color.GREEN);
                g.drawRoundRect(1, 1, 1, 1, 1, 1);
                g.drawOval(50, 50, 50, 50);
                Shape circle = new Ellipse2D.Double(100, 100, 100, 100);
                ((Graphics2D)g).draw(circle);
            }
        };
        canvas.setSize(mapPanel.getSize());
        mapPanel.add(canvas);
        return mapPanel;
    }

    private JPanel createTableTab() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane scrollTablePane = new JScrollPane(createTable());
        tablePanel.add(scrollTablePane);

        tablePanel.add(new JButton("test"), "West");

        return tablePanel;
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
        JTable table = new JTable(values,
                new Object[]{
                        getString("id_column_name"),
                        getString("name_column_name"),
                        getString("coordinate_x_column_name"),
                        getString("coordinate_y_column_name"),
                        getString("creation_date_column_name"),
                        getString("area_column_name"),
                        getString("population_column_name"),
                        getString("meters_above_sea_level_column_name"),
                        getString("establishment_date_column_name"),
                        getString("climate_column_name"),
                        getString("government_column_name"),
                        getString("governor_age_name"),
                        getString("governor_birthday_name")
                });
        table.setAutoCreateRowSorter(true);
        table.setRowSorter(setRowComparators(table.getModel()));
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }
    private RowSorter<TableModel> setRowComparators(TableModel tableModel) {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableModel);

        rowSorter.setComparator(0, Comparator.comparingLong(o -> (Long.parseLong((String)o))));
        rowSorter.setComparator(2, Comparator.comparingDouble(o -> (Float.parseFloat((String)o))));
        rowSorter.setComparator(3, Comparator.comparingInt(o -> (Integer.parseInt((String)o))));
        rowSorter.setComparator(5, Comparator.comparingLong(o -> (Long.parseLong((String)o))));
        rowSorter.setComparator(6, Comparator.comparingInt(o -> Integer.parseInt((String)o)));
        rowSorter.setComparator(7, Comparator.comparingLong(o -> Long.parseLong((String)o)));
        rowSorter.setComparator(11, Comparator.comparingLong(o -> Long.parseLong((String)o)));

        return rowSorter;
    }
    private void readCityCollection() {
        cities = new ArrayList<>();
        City city;
        for (int i=0;i<5;i++) {
            city = new City();
            city.setId(i+1L);
            city.setName("abc");
            city.getCoordinates().setX(0);
            city.getCoordinates().setY(2);
            city.setCreationDate(ZonedDateTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            city.setArea(i+99L);
            city.setPopulation(4);
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
}
