import java.util.HashMap;

public class City {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long area; //Значение поля должно быть больше 0
    private int population; //Значение поля должно быть больше 0
    private Long metersAboveSeaLevel;
    private java.time.LocalDate establishmentDate;
    private Climate climate; //Поле может быть null
    private Government government; //Поле может быть null
    private Human governor; //Поле не может быть null

    // подаётся уже уникальный ID!
    public void setId(final Long id) throws NullValueException, IncorrectValueException, NotUniqueIDException {
        if(id == null)
            throw new NullValueException();
        if(id <= 0)
            throw new IncorrectValueException("Значение ID должно быть больше 0");
        this.id = id;
    }

    public static boolean checkUniqueID(final Long id, final HashMap<Long, City> map) {
        return !map.containsKey(id);
    }

    public Long getId() {
        return id;
    }

    public void setName(final String name) throws NullValueException, EmptyValueException {
        if(name == null)
            throw new NullValueException();
        if(name.equals(""))
            throw new EmptyValueException("Отсутствует имя города");
        this.name = name;
    }

    public void setCoordinates(final Coordinates coordinates) throws NullValueException {
        if(coordinates == null)
            throw new NullValueException();
        this.coordinates = coordinates;
    }

    public void setCreationDate(final java.time.ZonedDateTime creationDate) throws NullValueException {
        if(creationDate == null)
            throw new NullValueException();
        this.creationDate = creationDate;
    }

    public void setArea(final long area) throws IncorrectValueException {
        if(area <= 0)
            throw new IncorrectValueException("Значение площади должно быть больше 0");
        this.area = area;
    }

    public void setPopulation(final int population) throws IncorrectValueException {
        if(population <= 0)
            throw new IncorrectValueException("Значение числа жителей должно быть больше 0");
        this.population = population;
    }

    public void setMetersAboveSeaLevel(final Long metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public void setEstablishmentDate(final java.time.LocalDate establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public void setClimate(final Climate climate) throws NullValueException {
        if(climate == null)
            throw new NullValueException();
        this.climate = climate;
    }

    public void setGovernment(final Government government) throws NullValueException {
        if(government == null)
            throw new NullValueException();
        this.government = government;
    }

    public void setGovernor(final Human governor) throws NullValueException {
        if(governor == null)
            throw new NullValueException();
        this.governor = governor;
    }
    @Override
    public String toString() {
        String s = "";
        s+="id: "+id+'\n';
        s+="name: "+name+'\n';
        s+="coordinates: "+coordinates+'\n';
        s+="creationDate: "+creationDate+'\n';
        s+="area: "+area+'\n';
        s+="population: "+population+'\n';
        s+="metersAboveSeaLevel: "+metersAboveSeaLevel+'\n';
        s+="establishmentDate: "+establishmentDate+'\n';
        s+="climate: "+climate+'\n';
        s+="government: "+government+'\n';
        s+="governor: "+governor+'\n';
        return s;
    }
}
