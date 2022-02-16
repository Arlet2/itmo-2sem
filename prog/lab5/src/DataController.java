import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataController {
    HashMap<Long, City> map;
    DataController() {
        map = new HashMap<>();
    }
    public void putCityToMap(final City city) {
        map.put(city.getId(), city);
    }
    /*
    public void readFromFile (final String path) {
        StringBuilder xmlString = new StringBuilder();
        try (Scanner scanner = new Scanner(Paths.get(path))){
            while(scanner.hasNextLine())
                xmlString.append(scanner.nextLine()).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile("<city>[\\n\\d\\w\\s<>/.\\-:+\\[\\]]*?</city>");
        Matcher matcher = pattern.matcher(xmlString);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find())
            list.add(xmlString.substring(matcher.start(), matcher.end()));
        City city;
        for(String i: list) {
            city = parseCity(i);
            if(city != null)
                putCityToMap(city);
        }
    }
    private City parseCity(final String xmlString) {
        return null;
    }
    */
    public void writeFile (final String path) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)){
            StringBuilder s = new StringBuilder();
            s.ensureCapacity(440*map.size());
            s.append("<xml>\n");
            for(City i: map.values())
                s.append(getCityXmlString(i));
            s.append("</xml>");
            bufferedOutputStream.write(s.toString().getBytes(),0,s.toString().getBytes().length);
            System.out.println("Данные были записаны в файл по пути "+path);
        } catch (FileNotFoundException e) {
            System.out.println("Данный файл не найден");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getCityXmlString(final City city) {
        StringBuilder xmlString = new StringBuilder();
        xmlString.ensureCapacity(440);
        xmlString.append("<city>\n");
        xmlString.append("<id>").append(city.getId()).append("</id>\n");
        xmlString.append("<name>").append(city.getName()).append("</name>\n");
        xmlString.append("<coordinates>\n<x>").append(city.getCoordinates().getX()).append("</x>\n<y>").append(city.getCoordinates().getY()).append("</y>\n</coordinates>\n");
        xmlString.append("<creation_date>").append(city.getCreationDate()).append("</creation_date>\n");
        xmlString.append("<area>").append(city.getArea()).append("</area>\n");
        xmlString.append("<population>").append(city.getPopulation()).append("</population>\n");
        xmlString.append("<meters_above_sea_level>").append(city.getMetersAboveSeaLevel()).append("</meters_above_sea_level>\n");
        xmlString.append("<establishment_date>").append(city.getEstablishmentDate()).append("</establishment_date>\n");
        xmlString.append("<climate>").append(city.getClimate()).append("</climate>\n");
        xmlString.append("<government>").append(city.getGovernment()).append("</government>\n");
        xmlString.append("<governor>\n<age>").append(city.getGovernor().getAge()).append("</age>\n<birthday>").append(city.getGovernor().getBirthday()).append("</birthday>\n</governor>\n");
        xmlString.append("</city>\n");
        return xmlString.toString();
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
    public City createCityByUser() {
        Scanner scanner = new Scanner(System.in);
        City city = new City();
        String input;
        try {
            System.out.println("Создание нового города...");
            city.generateID(map);

            System.out.print("Введите имя города: ");
            city.setName(scanner.nextLine());

            System.out.println("Введение координат города...");
            System.out.print("Введите координату x(>-407): ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("координата x");
            try {
                city.getCoordinates().setX(Float.parseFloat(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("координата x - число с плавающей точкой");
            }
            System.out.print("Введите координату y: ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("координата y");
            try {
                city.getCoordinates().setY(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("координата y - целое число");
            }

            System.out.print("Введите значение площади всего города: ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("площадь города");
            try {
                city.setArea(Long.parseLong(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("площадь города - целое число");
            }
            System.out.print("Введите количество жителей города: ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("население");
            try {
                city.setPopulation(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("количество жителей - целое число");
            }
            System.out.print("Введите высоту над уровнем моря: ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("высота над уровнем моря");
            try {
                city.setMetersAboveSeaLevel(Long.parseLong(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("высота над уровнем моря - целое число");
            }
            System.out.println("Введение даты основания города...");
            city.setEstablishmentDate(dateCreatorByUser(scanner, "основания города"));

            System.out.print("Введите климат города (");
            System.out.print(Climate.values()[0]);
            for (int i = 1; i < Climate.values().length; i++)
                System.out.print(", " + Climate.values()[i]);
            System.out.print("): ");
            input = scanner.nextLine().toUpperCase();
            if (input.equals("")) {
                System.out.println("Поле было пропущено.");
            } else {
                for (Climate i : Climate.values()) {
                    if (i.toString().equals(input)) {
                        city.setClimate(i);
                        break;
                    }
                }
                if (city.getClimate().equals(""))
                    System.out.println("Значение поля некорректно. Оно было пропущено.");
            }

            System.out.print("Введите тип правительства (");
            System.out.print(Government.values()[0]);
            for (int i = 1; i < Government.values().length; i++)
                System.out.print(", " + Government.values()[i]);
            System.out.print("): ");
            input = scanner.nextLine().toUpperCase();
            if (input.equals("")) {
                System.out.println("Поле было пропущено.");
            }
            else {
                for (Government i : Government.values()) {
                    if (i.toString().equals(input)) {
                        city.setGovernment(i);
                        break;
                    }
                }
                if (city.getGovernment().equals(""))
                    System.out.println("Значение поля некорректно. Оно было пропущено.");
            }

            System.out.println("Введение данных о правителе...");
            System.out.print("Введите возраст правителя: ");
            input = scanner.nextLine();
            if(input.equals(""))
                throw new EmptyValueException("возраст правителя");
            try {
                city.getGovernor().setAge(Long.parseLong(input));
            } catch (NumberFormatException e) {
                throw new IncorrectValueException("возраст - целое число");
            }
            System.out.println("Введение даты рождения правителя...");
            city.getGovernor().setBirthday(LocalDateTime.of(dateCreatorByUser(scanner, "рождения правителя"),
                    localTimeCreateByUser(scanner, "рождения правителя")));
        } catch (NullValueException e) {
            e.printStackTrace();
        } catch (IncorrectValueException e) {
            System.out.println("Некорректное значение: " + e.getMessage());
            if(isRepeatCreateCityByUser(scanner))
                return createCityByUser();
            else {
                System.out.println("Отмена создания города...");
                return null;
            }
        } catch (EmptyValueException e) {
            System.out.println("Поле "+e.getMessage()+" не может быть пустым");
            if(isRepeatCreateCityByUser(scanner))
                return createCityByUser();
            else {
                System.out.println("Отмена создания города...");
                return null;
            }
        }
        return city;
    }
    private boolean isRepeatCreateCityByUser(Scanner scanner) {
        System.out.println("Хотите повторить ввод (y/n)? ");
        String input = scanner.nextLine().toLowerCase();
        return input.equals("y");
    }
    private LocalDate dateCreatorByUser(final Scanner scanner, final String dateName) throws IncorrectValueException, EmptyValueException {
        int day, month, year;
        String input;
        System.out.print("Введите день "+dateName+"(число): ");
        input = scanner.nextLine();
        if(input.equals(""))
            throw new EmptyValueException("день "+dateName);
        try {
            day = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            throw new IncorrectValueException("день - целое число");
        }
        if(day > 31 || day < 1)
            throw new IncorrectValueException("день - число от 1 до 31");
        System.out.print("Введите месяц "+dateName+"(число): ");
        input = scanner.nextLine();
        if(input.equals(""))
            throw new EmptyValueException("месяц "+dateName);
        try {
            month = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            throw new IncorrectValueException("месяц - целое число");
        }
        if(month > 12 || month < 1)
            throw new IncorrectValueException("месяц - число от 1 до 12");
        System.out.print("Введите год "+dateName+"(число): ");
        input = scanner.nextLine();
        if(input.equals(""))
            throw new EmptyValueException("год "+dateName);
        try {
            year = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            throw new IncorrectValueException("год - целое число");
        }
        if(year <= 0)
            throw new IncorrectValueException("год - число положительное");
        if(!isNormalDate(day,month,year))
            throw new IncorrectValueException("невозможная дата");
        return LocalDate.of(year, month, day);
    }
    private boolean isNormalDate(final int day, final int month, final int year) {
        if(month == 2 && day == 29 && year % 4 == 0 && year % 400 == 0)
            return true;
        return ((month != 4 && month != 6 && month != 9 && month != 11) || day <= 30) && (month != 2 || day <= 29);
    }
    private LocalTime localTimeCreateByUser(final Scanner scanner,final String timeName) throws IncorrectValueException, EmptyValueException {
        int minute, hour;
        String input;
        System.out.print("Введите час "+timeName+"(число): ");
        input = scanner.nextLine();
        if(input.equals("")) {
            scanner.close();
            throw new EmptyValueException("час " + timeName);
        }
        try {
            hour = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            scanner.close();
            throw new IncorrectValueException("час - целое число");
        }
        if(hour < 0 || hour > 23) {
            scanner.close();
            throw new IncorrectValueException("час - число от 0 до 23");
        }
        System.out.print("Введите минуту "+timeName+"(число): ");
        input = scanner.nextLine();
        if(input.equals("")) {
            scanner.close();
            throw new EmptyValueException("минута " + timeName);
        }
        try {
            minute = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            scanner.close();
            throw new IncorrectValueException("минута - целое число");
        }
        if(minute < 0 || minute >= 60)
            throw new IncorrectValueException("минута - число от 0 до 59");
        return LocalTime.of(hour, minute);
    }
}
