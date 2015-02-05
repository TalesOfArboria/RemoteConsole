package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.commands.CommandDispatcher;

import org.bukkit.plugin.Plugin;

/**
 * Command dispatcher
 */
public class Dispatcher extends CommandDispatcher {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin.
     */
    public Dispatcher(Plugin plugin) {
        super(plugin);

        registerCommand(AddCommand.class);
        registerCommand(ConnectCommand.class);
        registerCommand(DelCommand.class);
        registerCommand(DisconnectCommand.class);
        registerCommand(ListCommand.class);
        registerCommand(StartupCommand.class);
    }
}
