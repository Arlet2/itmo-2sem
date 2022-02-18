package commands;

import data_classes.City;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

public abstract class Command implements Executable{
    private final String name;
    private final String description;
    private final String signature;
    Command (final String name,final String signature, final String description) {
        this.name = name;
        this.signature = signature;
        this.description = description;
    }
    protected long idValidator(CommandController commandController, String[] args) throws IncorrectArgumentException, MissingArgumentException {
        if(args.length < 2)
            throw new MissingArgumentException("id");
        if(args[1].isEmpty())
            throw new MissingArgumentException("id");
        long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IncorrectArgumentException("id - целое число");
        }
        if(id <= 0)
            throw new IncorrectArgumentException("id - число больше 0");
        return id;
    }
    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getDescription() {
        return description;
    }
}
