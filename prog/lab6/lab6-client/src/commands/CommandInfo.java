package commands;

import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.Serializable;

public class CommandInfo implements Serializable {
    private final String name;
    private final SendInfo sendInfo;
    private final ArgumentInfo[] argInfo;

    public CommandInfo(String name, SendInfo sendInfo, ArgumentInfo[] argInfo) {
        this.name = name;
        this.sendInfo = sendInfo;
        this.argInfo = argInfo;
    }

    /**
     * Validate id as argument in command
     * @param arg of command that user typed
     * @throws IncorrectArgumentException if id is incorrect in args
     * @throws MissingArgumentException if id is missing in args
     */
    public static void idValidator(String arg) throws IncorrectArgumentException, MissingArgumentException {
        long id;
        try {
            id = Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw new IncorrectArgumentException("id - целое число");
        }
        if(id <= 0)
            throw new IncorrectArgumentException("id - число больше 0");
    }
    public String getName() {
        return name;
    }

    public SendInfo getSendInfo() {
        return sendInfo;
    }

    public ArgumentInfo[] getArgInfo() {
        return argInfo;
    }

    public enum ArgumentInfo {
        ID,
        FLOAT,
        INT,
        STRING,
        CLIMATE,
        GOVERNMENT
    }
    public enum SendInfo {
        CITY,
        CITY_UPDATE,
        COMMANDS,
        EXIT
    }
}