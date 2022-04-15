package data_control;

import commands.CommandController;
import commands.CommandInfo;

import java.io.*;
import java.util.ArrayList;

/**
 * Control data reading from file
 */
public class FileController {
    /**
     * Current program controller
     */
    private final CommandController commandController;
    /**
     * LAST from reading script file commands with args as string
     */
    private ArrayList<String> strCommand;
    public FileController(CommandController commandController) {
        this.commandController = commandController;
    }

    /**
     * Read script file with commands on new lines
     * @param path of file with script
     * @return ArrayList of CommandInfo from script
     * @throws FileNotFoundException if script file not found
     */
    public ArrayList<CommandInfo> readScriptFile (String path) throws FileNotFoundException {
        strCommand = new ArrayList<>();
        ArrayList<CommandInfo> commandsInfo = new ArrayList<>();
        BufferedReader buffIn = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        buffIn.lines().forEach(string -> {
            String[] args;
            String s = string.replaceAll(" +", " ");
            args = s.split(" ");
            if (commandController.isValidCommand(args)) {
                    commandsInfo.add(commandController.parseCommand(args[0]));
                    strCommand.add(string);
            }
        });
        return commandsInfo;
    }

    public ArrayList<String> getStrCommand() {
        return strCommand;
    }
}
