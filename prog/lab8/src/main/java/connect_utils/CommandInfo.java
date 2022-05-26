package connect_utils;

import java.io.Serializable;

public class CommandInfo implements Serializable {
    private final String name;
    private final byte[] args;
    public CommandInfo(String name, byte[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public byte[] getArgs() {
        return args;
    }
}
