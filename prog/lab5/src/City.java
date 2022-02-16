import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class City {

    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates = new Coordinates(); //Поле не может быть null
    private java.time.ZonedDateTime creationDate = java.time.ZonedDateTime.now(); //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long area; //Значение поля должно быть больше 0
    private int population; //Значение поля должно быть больше 0
    private Long metersAboveSeaLevel;
    private java.time.LocalDate establishmentDate;
    private Climate climate; //Поле может быть null
    private Government government; //Поле может быть null
    private final Human governor = new Human(); //Поле не может быть null

    // подаётся уже уникальный ID!
    /**
     * Set id value in object
     * @param id unique id in desired collection for city
     * @throws NullValueException if input is null
     * @throws IncorrectValueException if input value is incorrect (&lt;= 0)
     */
    public void setId(final Long id) throws NullValueException, IncorrectValueException {
        if(id == null)
            throw new NullValueException();
        if(id <= 0)
            throw new IncorrectValueException("значение ID должно быть больше 0");
        this.id = id;
    }

    public void generateID(final HashMap<Long, City> map) {
        if(map.isEmpty()) {
            id = 1L;
            return;
        }
        ArrayList<Long> ids = new ArrayList<>();
        for(City i: map.values())
            ids.add(i.getId());
        ids.sort(Long::compare);
        // если минимальное значение не минимально возможное, то заполняем к 1 от минимального
        if(ids.get(0) != 1)
            id = ids.get(0)-1;
        // если минимальное значение уже единица, то пытаемся заполнить пробелы после минимального
        else {
            for (long i = 2; ; i++) {
                if (checkUniqueID(i, map)) {
                    id = i;
                    break;
                }
            }
        }
    }

    /**
     * Checking unique of the giving id in the giving collection
     * @param id city id for check
     * @param map collection where we'll check
     * @return <b>true</b> if id in collection is unique <p>else <b>false</b>
     */
    public static boolean checkUniqueID(final Long id, final HashMap<Long, City> map) {
        return !map.containsKey(id);
    }

    /**
     * Get id of this city
     * @return id of this city
     */
    public Long getId() {
        return id;
    }

    public void setName(final String name) throws NullValueException, IncorrectValueException, EmptyValueException {
        if(name == null)
            throw new NullValueException();
        if(name.equals(""))
            throw new EmptyValueException("имя");
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCreationDate(final java.time.ZonedDateTime creationDate) throws NullValueException {
        if(creationDate == null)
            throw new NullValueException();
        this.creationDate = creationDate;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setArea(final long area) throws IncorrectValueException {
        if(area <= 0)
            throw new IncorrectValueException("значение площади должно быть больше 0");
        this.area = area;
    }

    public long getArea() {
        return area;
    }

    public void setPopulation(final int population) throws IncorrectValueException {
        if(population <= 0)
            throw new IncorrectValueException("значение числа жителей должно быть больше 0");
        this.population = population;
    }

    public int getPopulation() {
        return population;
    }

    public void setMetersAboveSeaLevel(final Long metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public Long getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    public void setEstablishmentDate(final java.time.LocalDate establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public LocalDate getEstablishmentDate() {
        return establishmentDate;
    }

    public void setClimate(final Climate climate) {
        this.climate = climate;
    }

    public String getClimate() {
        if(climate == null)
            return "";
        return climate.toString();
    }

    public void setGovernment(final Government government) {
        this.government = government;
    }

    public String getGovernment() {
        if(government == null)
            return "";
        return government.toString();
    }

    public Human getGovernor() {
        return governor;
    }

    /**
     * @deprecated
     * @return all data of this city
     */

    @Override
    public String toString() {
        return "city:\n" +
                "id: " + id + "\n" +
                "name: " + name + "\n" +
                "coordinates:\n" + coordinates + "\n" +
                "creationDate: " + creationDate + "\n" +
                "area: " + area + "\n" +
                "population: " + population + "\n" +
                "metersAboveSeaLevel: " + metersAboveSeaLevel + "\n" +
                "establishmentDate: " + establishmentDate + "\n" +
                "climate: " + climate + "\n" +
                "government: " + government + "\n" +
                "governor:\n" + governor + "\n";
    }
}
