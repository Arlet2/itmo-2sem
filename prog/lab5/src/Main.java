import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        //java.time.LocalDate a = java.time.LocalDate.now(ZoneId.);
        HashMap<Long, City> map = new HashMap<>();
        City omsk = new City();
        City orsk = new City();
        try {
            omsk.setId(56L);
            orsk.setId(57L);
            omsk.setGovernor(new Human(54L, LocalDateTime.now()));
            omsk.setClimate(Climate.RAIN_FOREST);
        }catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        map.put(omsk.getId(), omsk);
        map.put(orsk.getId(), orsk);
        System.out.println(map.get(56L)+""+map.get(57L));
        System.out.println(City.checkUniqueID(57L,map));
        for (City city : map.values()) {
            System.out.println(city);
        }
    }
}
