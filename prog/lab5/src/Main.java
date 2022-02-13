import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        java.time.LocalDate a = java.time.LocalDate.now();
        System.out.println(a);
        DataController dc = new DataController();
        dc.readFromFile(args[0]);
        System.out.println(dc.sortMap());
    }
}
