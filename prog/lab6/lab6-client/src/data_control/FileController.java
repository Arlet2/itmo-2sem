package data_control;

import commands.CommandController;
import commands.CommandInfo;

import java.io.*;
import java.util.ArrayList;

public class FileController {
    private final CommandController commandController;
    private ArrayList<String> strCommand;
    public FileController(CommandController commandController) {
        this.commandController = commandController;
    }
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
