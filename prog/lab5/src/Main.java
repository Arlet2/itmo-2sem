import commands.CommandController;
import commands.HelpCommand;
import data_control.DataController;

public class Main {
    public static void main(String[] args) {
        CommandController cc = new CommandController(new DataController(args[0]));
        cc.listenConsole();
    }
}
