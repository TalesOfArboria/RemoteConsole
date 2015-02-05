package com.jcwhatever.remoteconsole.data;

/**
 * A serializable type representing a console command.
 */
public class ConsoleCommand {

    public String command;

    public ConsoleCommand() {}

    public ConsoleCommand(String cmd) {
        this.command = cmd;
    }

}
