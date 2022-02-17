package commands;

import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandController {
    protected static final int MAX_COMMAND_IN_HISTORY = 13;
    private final DataController dataController;
    private final ArrayList<Command> history;
    private final ArrayList<Command> allCommands;
    public CommandController(final DataController dataController) {
        this.dataController = dataController;
        history = new ArrayList<>();
        allCommands = new ArrayList<>();
        commandInit();
    }

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

    public void listenConsole() {
        Scanner scanner = new Scanner(System.in);
        String[] input;
        Command command;
        while(true) {
            System.out.print("$ ");
            input = scanner.nextLine().split(" ");
            try {
                command = searchCommand(input[0]);
            } catch(UnknownCommandException e) {
                System.out.println("Неизвестная команда. Используйте команду help для отображения списка команд.");
                continue;
            }
            try {
                invoke(command, input);
            } catch (MissingArgumentException e) {
                System.out.println("Отсутствует аргумент "+ e.getMessage() + " для команды "+command.getName());
            } catch (IncorrectArgumentException e) {
                System.out.println("Некорректный аргумент "+ e.getMessage() + " для команды "+ command.getName());
            }
        }
    }
    protected void invoke(final Command command,final String[] args) throws MissingArgumentException, IncorrectArgumentException {
        command.execute(this, args);
        addToHistory(command);
    }
    private void addToHistory (Command command) {
        if(history.size() == MAX_COMMAND_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }
    protected ArrayList<Command> getHistory() {
        return history;
    }

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
