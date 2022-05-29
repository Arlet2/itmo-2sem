package client.data_control;

import exceptions.ConfigFileNotFoundException;
import exceptions.MissingArgumentException;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Control data reading from file
 */
public class FileController {

    /**
     * Read script file with commands on new lines
     *
     * @param path of file with script
     * @return ArrayList of CommandInfo from script
     * @throws FileNotFoundException if script file not found
     */
    public ArrayList<String> readScriptFile(String path) throws FileNotFoundException {
        ArrayList<String> commandsInfo = new ArrayList<>();
        BufferedReader buffIn = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        buffIn.lines().forEach(string -> {
            String s = string.replaceAll(" +", " ");
            commandsInfo.add(s.split(" ")[0]);
        });
        return commandsInfo;
    }

    /**
     * Read config file with "config.excalibbur".
     * File need to have "port: *digits*" and "address: *IP address/domain*"
     *
     * @throws MissingArgumentException if port or address not found in file
     */
    public InetSocketAddress readConfig() throws MissingArgumentException, ConfigFileNotFoundException {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream("config.excalibbur"));
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException("configuration_file_not_found");
        }
        StringBuilder s = new StringBuilder();
        int port;
        String address;
        while (scanner.hasNextLine())
            s.append(scanner.nextLine()).append("\n");
        Matcher matcher = Pattern.compile("(?<=port:)\\d+|(?<=port:\\s)\\d+|(?<=port:\\s{2})\\d+",
                Pattern.CASE_INSENSITIVE).matcher(s.toString());
        scanner.close();
        if (matcher.find())
            port = Integer.parseInt(s.substring(matcher.start(), matcher.end()));
        else
            throw new MissingArgumentException("port_not_found");
        matcher = Pattern.compile("(?<=address:)[\\w\\d.]+|(?<=address:\\s)[\\w\\d.]+|(?<=address:\\s{2})[\\w\\d.]+",
                Pattern.CASE_INSENSITIVE).matcher(s.toString());
        if (matcher.find())
            address = s.substring(matcher.start(), matcher.end());
        else
            throw new MissingArgumentException("address_not_found");
        return new InetSocketAddress(address, port);
    }

}
