import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataController {
    private final HashMap<Long, City> map;
    private final FileController fileController;
    private final ConsoleController consoleController;
    DataController() {
        map = new HashMap<>();
        fileController = new FileController(this);
        consoleController = new ConsoleController(this);
    }
    public void putCityToMap(final City city) {
        map.put(city.getId(), city);
    }
    public HashMap<Long, City> getMap() {
        return map;
    }
    public Collection<City> sortMap() {
        ArrayList<City> arrayList = new ArrayList<>(map.values());
        arrayList.sort((o1, o2) -> {
            if(o1.getId() > o2.getId())
                return 1;
            else if(o1.getId() < o2.getId())
                return -1;
            return 0;
        });
        return arrayList;
    }

}
