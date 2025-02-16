package org.ru.dodonov;

public enum Command {
    ADD("add"),
    LIST("list"),
    EDIT("edit"),
    DELETE("delete"),
    FILTER("filter"),
    SORT("sort"),
    EXIT("exit");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public static Command fromString(String text) {
        for (Command command : Command.values()) {
            if (command.commandName.equalsIgnoreCase(text)) {
                return command;
            }
        }
        return null;
    }
}
