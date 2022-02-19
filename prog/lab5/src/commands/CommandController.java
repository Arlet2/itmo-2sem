package commands;

import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * controls execution of all commands
 */
public class CommandController {
    /**
     * max value of commands for keep in history
     */
    protected static final int MAX_COMMANDS_IN_HISTORY = 13;
    /**
     * that controls data for program
     */
    private final DataController dataController;
    /**
     * history of all commands that was used
     */
    private final ArrayList<Command> history;
    /**
     * collection of all commands that user can used
     */
    private final ArrayList<Command> allCommands;
    public CommandController (final DataController dataController) {
        this.dataController = dataController;
        history = new ArrayList<>();
        allCommands = new ArrayList<>();
        commandInit();
    }

    /**
     * Initialization commands to allCommands that can be used by user
     */
    private void commandInit() {
        allCommands.add(new HelpCommand());
        allCommands.add(new InfoCommand());
        allCommands.add(new ShowCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new RemoveKeyCommand());
        allCommands.add(new ClearCommand());
        allCommands.add(new SaveCommand());
        allCommands.add(new ExecuteScriptCommand());
        allCommands.add(new ExitCommand());
        allCommands.add(new HistoryCommand());
        allCommands.add(new ReplaceIfGreaterCommand());
        allCommands.add(new RemoveLowerKeyCommand());
        allCommands.add(new FilterGreaterThanClimateCommand());
        allCommands.add(new PrintAscendingCommand());
        allCommands.add(new PrintFieldAscendingGovernment());
    }

    /**
     * Listen console for command reading until user type exit command
     */
    public void listenConsole() {
        Scanner scanner = new Scanner(System.in);
        String[] input;
        Command command;
        while(true) {
            System.out.print("$ ");
            input = scanner.nextLine().split(" ");
            try {
                command = searchCommand(input[0].toLowerCase());
            } catch(UnknownCommandException e) {
                System.out.println("Неизвестная команда. Используйте команду help для отображения списка команд.");
                continue;
            }
            try {
                invoke(command, input);
            } catch (MissingArgumentException e) {
                System.out.println("Отсутствует аргумент "+ e.getMessage());
            } catch (IncorrectArgumentException e) {
                System.out.println("Некорректный аргумент: "+ e.getMessage());
            }
        }
    }

    /**
     * Execute command and add it in history
     * @param command that need to invoke
     * @param args for this command
     * @throws MissingArgumentException if requiring args is missing
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    protected void invoke(final Command command,final String[] args) throws MissingArgumentException, IncorrectArgumentException {
        command.execute(this, args);
        addToHistory(command);
    }

    /**
     * Add command to history and if history is overflow delete first command
     * @param command that be added to history
     */
    private void addToHistory (Command command) {
        if(history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }
    protected ArrayList<Command> getHistory() {
        return history;
    }

    /**
     * Parse string to command
     * @param name of command
     * @return command
     * @throws UnknownCommandException if name of command doesn't equal with name in command's constructor
     */
    protected Command searchCommand (final String name) throws UnknownCommandException {
        for(Command i: allCommands) {
            if(i.getName().equals(name))
                return i;
        }
        throw new UnknownCommandException();
    }
    protected DataController getDataController() {
        return dataController;
    }

    public ArrayList<Command> getAllCommands() {
        return allCommands;
    }
}
