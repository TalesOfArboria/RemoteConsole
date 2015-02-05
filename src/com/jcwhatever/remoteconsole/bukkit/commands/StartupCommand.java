package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.remoteconsole.bukkit.Lang;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;
import com.jcwhatever.remoteconsole.bukkit.connect.ServerInfo;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="startup",
        staticParams={"serverName", "true|false"},
        description="Set an automatic connection to a server when Minecraft server is started.",
        paramDescriptions = {
                "serverName= The name of the remote console server.",
                "true|false= 'true' to auto connect to the remote console when " +
                        "the server starts. Otherwise 'false'."
        })
public class StartupCommand extends AbstractCommand {

    @Localizable static final String _SERVER_NOT_FOUND =
            "A remote console server named '{0: server name}' was not found.";

    @Localizable static final String _ENABLED =
            "Remote console server '{0: server name}' will be auto connected to " +
                    "whenever the Minecraft server is started.";

    @Localizable static final String _DISABLED =
            "Remote console server '{0: server name}' auto connect disabled.";

    @Override
    public void execute(final CommandSender sender, CommandArguments args) throws CommandException {

        String serverName = args.getName("serverName", 64);
        boolean isStartupConnect = args.getBoolean("true|false");

        final ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        ServerInfo info = manager.get(serverName);
        if (info == null) {
            tellError(sender, Lang.get(_SERVER_NOT_FOUND, serverName));
            return; // finish
        }

        info.setStartupConnect(isStartupConnect);

        if (isStartupConnect)
            tellSuccess(sender, Lang.get(_ENABLED, serverName));
        else
            tellSuccess(sender, Lang.get(_DISABLED, serverName));
    }
}