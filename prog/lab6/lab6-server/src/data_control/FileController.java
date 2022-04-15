package data_control;

import data_classes.City;
import data_classes.Climate;
import data_classes.Government;
import exceptions.*;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses for file writing/reading
 */
public class FileController {
    /**
     * Value of buffer for file writing (minimum symbols of city's data in xml)
     */
    private final int MIN_BUFFER_FOR_CITY = 440;
    /**
     * that uses by program
     */
    private final DataController dataController;

    /**
     * @param dataController that uses by program
     */
    FileController(DataController dataController) {
        this.dataController = dataController;
    }

    /**
     * Read file and return xml in string
     * @param path of xml file that program will read
     * @return string of xml data
     */
    private String readXml(final String path) throws IOException{
        StringBuilder xmlString = new StringBuilder();
        Scanner scanner = new Scanner(Paths.get(path));
        while(scanner.hasNextLine())
            xmlString.append(scanner.nextLine()).append("\n");
        scanner.close();
        return xmlString.toString();
    }

    /**
     * Read file and add cities to collection
     * @param path of file that program will read
     */
    protected void readFromFile (final String path) throws IOException {
        String xmlString = readXml(path);
        Matcher matcher = Pattern.compile("(?<=<city>)[\\S\\s]*?(?=</city>)", Pattern.CASE_INSENSITIVE).matcher(xmlString);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find())
            list.add(xmlString.substring(matcher.start(), matcher.end()));
        City city;
        for(String i: list) {
            try {
                city = parseCity(i);
                dataController.putCityToMap(city);
            } catch (SoManyArgumentsException | EmptyValueException e) {
                dataController.getCommandController().getLogger().log(Level.WARNING, "Ошибка считывания в файле: ошибка аргументов в этом городе: "+
                        e.getMessage()+"\nГород был пропущен");
            } catch (IncorrectValueException e) {
                dataController.getCommandController().getLogger().log(Level.WARNING, "Ошибка считывания в файле: некорректное значение: "+
                        e.getMessage()+"\nГород был пропущен");
            } catch (NullValueException e) {
                e.printStackTrace();
            } catch (NotUniqueIDException e) {
                dataController.getCommandController().getLogger().log(Level.WARNING, "Ошибка считывания в файле: значение ID не уникально. Город был пропущен");
            }
        }
    }

    /**
     * Write data in xml to file
     * @param path of file that program will write
     */
    protected void writeFile (final String path) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)){
            StringBuilder s = new StringBuilder();
            s.ensureCapacity(MIN_BUFFER_FOR_CITY*dataController.getMap().size());
            s.append("<xml>\n");
            for(City i: dataController.getMap().values())
                s.append(getCityXmlString(i));
            s.append("</xml>");
            bufferedOutputStream.write(s.toString().getBytes(),0,s.toString().getBytes().length);
        } catch (FileNotFoundException e) {
            dataController.getCommandController().getLogger().log(Level.WARNING, "Данный файл не найден");
        } catch (IOException e) {
            dataController.getCommandController().getLogger().log(Level.WARNING, "Не получилось открыть файл");
        }

    }

    /**
     * Convert city's data to xml string
     * @param city that need to be converted
     * @return xml string of city's data
     */
    private String getCityXmlString(final City city) {
        StringBuilder xmlString = new StringBuilder();
        xmlString.ensureCapacity(MIN_BUFFER_FOR_CITY);
        xmlString.append("<city>\n");
        xmlString.append("<id>").append(city.getId()).append("</id>\n");
        xmlString.append("<name>").append(city.getName()).append("</name>\n");
        xmlString.append("<coordinates>\n<x>").append(city.getCoordinates().getX()).append("</x>\n<y>").append(city.getCoordinates().getY()).append("</y>\n</coordinates>\n");
        xmlString.append("<creation_date>").append(city.getCreationDate()).append("</creation_date>\n");
        xmlString.append("<area>").append(city.getArea()).append("</area>\n");
        xmlString.append("<population>").append(city.getPopulation()).append("</population>\n");
        xmlString.append("<meters_above_sea_level>").append(city.getMetersAboveSeaLevel()).append("</meters_above_sea_level>\n");
        xmlString.append("<establishment_date>").append(city.getEstablishmentDate()).append("</establishment_date>\n");
        xmlString.append("<climate>").append(city.getClimateString()).append("</climate>\n");
        xmlString.append("<government>").append(city.getGovernmentString()).append("</government>\n");
        xmlString.append("<governor>\n<age>").append(city.getGovernor().getAge()).append("</age>\n<birthday>").append(city.getGovernor().getBirthday()).append("</birthday>\n</governor>\n");
        xmlString.append("</city>\n");
        return xmlString.toString();
    }

    /**
     * Get next matching from string with pattern
     * @param xmlString that have data
     * @param pattern that is used for searching in string
     * @return string with used pattern
     */
    private String getMatch (final String xmlString, final String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(xmlString);
        matcher.find();
        return xmlString.substring(matcher.start(), matcher.end());
    }

    /**
     * Count matches
     * @param xmlString where can be existed matches
     * @param pattern that is used for searching
     * @return count of matches
     */
    private int countMatches (final String xmlString, final String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(xmlString);
        int counter = 0;
        while(matcher.find()) counter++;
        return counter;
    }

    /**
     * Parse city from xml
     * @param xmlString that keep city's data
     * @return city object
     * @throws SoManyArgumentsException if so many tags with same names
     * @throws EmptyValueException if required tag is empty
     * @throws IncorrectValueException if value in tag is incorrect
     * @throws NullValueException if required tag is empty
     * @throws NotUniqueIDException if id is not unique for this collection
     */
    private City parseCity(final String xmlString) throws SoManyArgumentsException, EmptyValueException, IncorrectValueException, NullValueException, NotUniqueIDException {
        String pattern, extraPattern, tempStr;
        City city = new City();

        // id
        pattern = "(?<=<id>)[\\S\\s]*?(?=</id>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов id");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент id");
        try {
            tempStr = getMatch(xmlString, pattern);
            if(City.checkUniqueID(Long.parseLong(tempStr), dataController.getMap()))
                city.setId(Long.parseLong(tempStr));
            else
                throw new NotUniqueIDException();
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("id - целое число");
        }

        // name
        pattern = "(?<=<name>)[\\S\\s]*?(?=</name>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов name");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент name");
        city.setName(getMatch(xmlString, pattern));

        // coordinates
        pattern = "(?<=<coordinates>)[\\S\\s]*?(?=</coordinates>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов coordinates");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент coordinates");
        // x
        extraPattern = "(?<=<x>)[\\S\\s]*?(?=</x>)";
        if(countMatches(getMatch(xmlString, pattern), extraPattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов x");
        else if(countMatches(getMatch(xmlString, pattern), extraPattern) == 0)
            throw new EmptyValueException("отсутствует аргумент x");
        try {
            city.getCoordinates().setX(Float.parseFloat(getMatch(getMatch(xmlString, pattern), extraPattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("x - число c плавающей точкой");
        }

        // y
        extraPattern = "(?<=<y>)[\\S\\s]*?(?=</y>)";
        if(countMatches(getMatch(xmlString, pattern), extraPattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов y");
        else if(countMatches(getMatch(xmlString, pattern), extraPattern) == 0)
            throw new EmptyValueException("отсутствует аргумент y");
        try {
            city.getCoordinates().setY(Integer.parseInt(getMatch(getMatch(xmlString, pattern), extraPattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("y - целое число");
        }

        // creationDate
        pattern = "(?<=<creation_date>)[\\S\\s]*?(?=</creation_date>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов creation_date");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент creation_date");
        try {
            city.setCreationDate(ZonedDateTime.parse(getMatch(xmlString, pattern)));
        } catch(DateTimeParseException e) {
            throw new IncorrectValueException("некорректный формат даты");
        }

        // area
        pattern = "(?<=<area>)[\\S\\s]*?(?=</area>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов area");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент area");
        try {
            city.setArea(Long.parseLong(getMatch(xmlString, pattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("area - целое число");
        }

        // population
        pattern = "(?<=<population>)[\\S\\s]*?(?=</population>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов population");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент population");
        try {
            city.setPopulation(Integer.parseInt(getMatch(xmlString, pattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("population - целое число");
        }

        // meters_above_sea_level
        pattern = "(?<=<meters_above_sea_level>)[\\S\\s]*?(?=</meters_above_sea_level>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов meters_above_sea_level");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент meters_above_sea_level");
        try {
            city.setMetersAboveSeaLevel(Long.parseLong(getMatch(xmlString, pattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("meters_above_sea_level - целое число");
        }

        // establishment_date
        pattern = "(?<=<establishment_date>)[\\S\\s]*?(?=</establishment_date>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов establishment_date");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент establishment_date");
        try {
            city.setEstablishmentDate(LocalDate.parse(getMatch(xmlString, pattern)));
        } catch(DateTimeParseException e) {
            throw new IncorrectValueException("некорректный формат даты");
        }

        // climate
        pattern = "(?<=<climate>)[\\S\\s]*?(?=</climate>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов climate");
        tempStr = getMatch(xmlString, pattern);
        if(!tempStr.equals("")) {
            for (Climate i : Climate.values()) {
                if (i.toString().equals(tempStr)) {
                    city.setClimate(i);
                    break;
                }
            }
            if (city.getClimateString().equals(""))
                dataController.getCommandController().getLogger().log(Level.INFO, "Значение аргумента climate некорректно. Поле было пропущено.");
        }

        // government
        pattern = "(?<=<government>)[\\S\\s]*?(?=</government>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов government");
        tempStr = getMatch(xmlString, pattern);
        if(!tempStr.equals("")) {
            for (Government i : Government.values()) {
                if (i.toString().equals(tempStr)) {
                    city.setGovernment(i);
                    break;
                }
            }
            if (city.getGovernmentString().equals(""))
                dataController.getCommandController().getLogger().log(Level.INFO, "Значение аргумента government некорректно. Поле было пропущено.");
        }

        // governor
        pattern = "(?<=<governor>)[\\S\\s]*?(?=</governor>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов governor");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент governor");
        // age
        extraPattern = "(?<=<age>)[\\S\\s]*?(?=</age>)";
        if(countMatches(getMatch(xmlString, pattern), extraPattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов age");
        else if(countMatches(getMatch(xmlString, pattern), extraPattern) == 0)
            throw new EmptyValueException("отсутствует аргумент age");
        try {
            city.getGovernor().setAge(Long.parseLong(getMatch(getMatch(xmlString, pattern), extraPattern)));
        } catch (NumberFormatException e) {
            throw new IncorrectValueException("age - целое число");
        }

        // birthday
        extraPattern = "(?<=<birthday>)[\\S\\s]*?(?=</birthday>)";
        if(countMatches(xmlString, pattern) > 1)
            throw new SoManyArgumentsException("слишком много аргументов birthday");
        else if(countMatches(xmlString, pattern) == 0)
            throw new EmptyValueException("отсутствует аргумент birthday");
        try {
            city.getGovernor().setBirthday(LocalDateTime.parse(getMatch(getMatch(xmlString, pattern), extraPattern)));
        } catch(DateTimeParseException e) {
            throw new IncorrectValueException("некорректный формат даты");
        }

        return city;
    }
}
