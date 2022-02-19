package commands;

import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ExecuteScriptCommand extends Command {
    /**
     * count of usage execute_script
     */
    private int recursionCounter = 0;
    /**
     * stop value for recursion
     */
    public final static int RECURSION_INTERRUPT = 10;
    ExecuteScriptCommand() {
        super("execute_script", "file_name","исполняет скрипт из указанного файла");
    }

    /**
     * execute script from file
     * @param commandController that uses for program
     * @param args file name
     * @throws MissingArgumentException if file_name is missing
     * @throws IncorrectArgumentException if file_name if empty/file doesn't exist
     */
    @Override
    public void execute(CommandController commandController, String[] args) throws MissingArgumentException, IncorrectArgumentException {
        if(args.length < 2)
            throw new MissingArgumentException("file_name");
        if(args[1].isEmpty())
            throw new MissingArgumentException("file_name");
        String commandString = readFile(args[1]);
        invokeCommands(commandController, commandString);
    }
    private String readFile (final String path) throws IncorrectArgumentException {
        StringBuilder commandString = new StringBuilder();
        try (Scanner scanner = new Scanner(Paths.get(path))){
            while(scanner.hasNextLine())
                commandString.append(scanner.nextLine()).append("\n");
        } catch (IOException e) {
            throw new IncorrectArgumentException("данный файл не найден");
        }
        return commandString.toString();
    }

    private void invokeCommands (CommandController commandController, final String commandString) throws MissingArgumentException, IncorrectArgumentException {
        String[] commands = commandString.split("\n");
        String[] input;
        for (String i: commands) {
            input = i.split(" ");
            try {
                if (input[0].toLowerCase().equals(getName()))
                    recursionCounter++;
                if (recursionCounter == RECURSION_INTERRUPT) {
                    System.out.println("Прерывание рекурсии...");
                    System.out.println("Всего рекурсивных вызовов "+ recursionCounter);
                    return;
                }
                commandController.invoke(commandController.searchCommand(input[0].toLowerCase()), input);
            } catch (UnknownCommandException e) {
                System.out.println("Неизвестная команда "+input[0]);
            }
        }
    }
}
