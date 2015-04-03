package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.observer.result.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.result.Result;
import com.jcwhatever.remoteconsole.bukkit.Lang;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionThread;
import com.jcwhatever.remoteconsole.bukkit.connect.ServerInfo;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="del",
        staticParams="serverName",
        description="Remove a remote console server.",
        paramDescriptions = {
                "serverName= The name of the remote console server."
        })
public class DelCommand extends AbstractCommand {

    @Localizable static final String _SERVER_NOT_FOUND =
            "A remote console server named '{0: server name}' was not found.";

    @Localizable static final String _FAILED =
            "Failed to remove remote console server.";

    @Localizable static final String _SUCCESS =
            "Remote console server '{0: server name}' removed.";

    @Override
    public void execute(final CommandSender sender, CommandArguments args) throws CommandException {

        final String serverName = args.getName("serverName");

        final ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        ServerInfo info = manager.get(serverName);
        if (info == null) {
            tellError(sender, Lang.get(_SERVER_NOT_FOUND, serverName));
            return; // finish
        }

        manager.disconnect(serverName)
                .onResult(new FutureSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {

                        if (manager.remove(serverName)) {
                            tellSuccess(sender, Lang.get(_SUCCESS, serverName));
                        } else {
                            tellError(sender, Lang.get(_FAILED, serverName));
                        }

                    }
                });
    }
}