package commands;

import connection_control.Request;
import exceptions.IncorrectArgumentException;
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
        super("execute_script", "file_name", "исполняет скрипт из указанного файла", CommandInfo.SendInfo.COMMANDS,
                new CommandInfo.ArgumentInfo[]{CommandInfo.ArgumentInfo.STRING}, false);
    }

    /**
     * execute script from file
     *
     * @param commandController that uses for program
     * @param args              file name
     * @throws IncorrectArgumentException if file_name if empty/file doesn't exist
     */
    @Override
    public String execute(CommandController commandController, String[] args) throws IncorrectArgumentException,
            IOException, ClassNotFoundException {
        commandController.sendOK();
        Request request = commandController.receiveRequest();
        Command command = null;
        String[] cArgs;
        while (!request.getRequestCode().equals(Request.RequestCode.OK)) {
            cArgs = request.getMsg().split(" ");
            try {
                command = commandController.searchCommand(cArgs[0]);
            } catch (UnknownCommandException e) {

            }
            if (command != null) {
                if (command.getName().equals("execute_script"))
                    recursionCounter++;
                if (recursionCounter >= RECURSION_INTERRUPT) {
                    commandController.sendError("Глубина рекурсии слишком большая (рекурсия может быть глубиной до "
                            + RECURSION_INTERRUPT + ".\nВыход из рекурсии..");
                }
                commandController.invoke(command, cArgs);
            }
            request = commandController.receiveRequest();
        }
        recursionCounter = 0;
        commandController.sendOK();
        return null;
    }


}
