package server.commands;

import connect_utils.*;
import exceptions.IncorrectArgumentException;
import server.connection_control.User;

import java.io.IOException;

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
     * @param programController that uses for program
     * @param args              file name
     * @throws IncorrectArgumentException if file_name if empty/file doesn't exist
     */
    @Override
    public String execute(User user, ProgramController programController, String[] args)
            throws IncorrectArgumentException,
            IOException, ClassNotFoundException {
        programController.getConnectionController().getRequestController().sendOK(user.getSocket());
        Request request = programController.getConnectionController().getRequestController()
                .receiveRequest(user.getSocket());
        Command command;
        String[] cArgs;
        String reply = null;
        while (!request.getRequestCode().equals(Request.RequestCode.OK)) {
            cArgs = request.getMsg().split(" ");
            command = programController.searchCommand(cArgs[0]);
            if (command != null) {
                if (command.getName().equals("execute_script"))
                    recursionCounter++;
                if (recursionCounter >= RECURSION_INTERRUPT) {
                    programController.getConnectionController().getRequestController()
                            .sendError(user.getSocket(), "Глубина рекурсии слишком большая " +
                                    "(рекурсия может быть глубиной до "
                                    + RECURSION_INTERRUPT + ".\nВыход из рекурсии..");
                }
                try {
                    reply = programController.invoke(user, command, cArgs);
                } catch (IncorrectArgumentException e) {
                    programController.getConnectionController().getRequestController()
                            .sendError(user.getSocket(), "получен некорректный аргумент - "+e.getMessage());
                } catch (IOException e) {
                    user.disconnect();
                    return null;
                } catch (ClassNotFoundException ignored) {

                }
                if (reply != null)
                    programController.getConnectionController().getRequestController()
                            .sendReply(user.getSocket(), reply);
            }
            request = programController.getConnectionController().getRequestController()
                    .receiveRequest(user.getSocket());
        }
        recursionCounter = 0;
        programController.getConnectionController().getRequestController()
                .sendOK(user.getSocket());
        return null;
    }


}
