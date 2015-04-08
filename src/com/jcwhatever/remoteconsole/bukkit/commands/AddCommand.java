package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.remoteconsole.bukkit.Lang;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;
import com.jcwhatever.remoteconsole.bukkit.connect.ServerInfo;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="add",
        staticParams={"serverName", "address", "port"},
        description="Add a remote console server.",
        paramDescriptions = {
                "serverName= The name of the remote console server. {NAME}",
                "address= The address of the remote console server.",
                "port= The port number of the remote console server."
        })
public class AddCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _SERVER_EXISTS =
            "A remote console server named '{0: server name}' already exists.";

    @Localizable static final String _FAILED =
            "Failed to add remote console server.";

    @Localizable static final String _SUCCESS =
            "Remote console server '{0: server name}' added.";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        String serverName = args.getName("serverName", 64);
        String address = args.getString("address");
        int port = args.getInteger("port");

        final ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        ServerInfo info = manager.get(serverName);
        if (info != null) {
            tellError(sender, Lang.get(_SERVER_EXISTS, serverName));
            return; // finish
        }

        ServerInfo connection = manager.add(serverName, address, port);
        if (connection == null){
            tellError(sender, Lang.get(_FAILED, serverName));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, serverName));
    }
}