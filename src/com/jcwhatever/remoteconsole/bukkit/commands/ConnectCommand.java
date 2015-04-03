package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;
import com.jcwhatever.nucleus.utils.observer.result.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.result.Result;
import com.jcwhatever.remoteconsole.bukkit.Lang;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionThread;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="connect",
        staticParams="serverName",
        description="Connect to a remote console.",
        paramDescriptions = {
                "serverName= The name of the remote console server."
        })
public class ConnectCommand extends AbstractCommand {

    @Localizable static final String _SERVER_NOT_FOUND =
            "A remote console server named '{0: server name}' was not found.";

    @Localizable static final String _FAILED =
            "Failed to connect to remote console server.";

    @Localizable static final String _SUCCESS =
            "Connected to remote console server '{0: server name}'.";

    @Override
    public void execute(final CommandSender sender, CommandArguments args) throws CommandException {

        final String serverName = args.getName("serverName");

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        Future<ConnectionThread> result = manager.connect(serverName);

        result
                .onError(new FutureSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {

                        if (result.hasResult())
                            tellError(sender, Lang.get(_FAILED, serverName));
                        else
                            tellError(sender, Lang.get(_SERVER_NOT_FOUND, serverName));
                    }
                })
                .onSuccess(new FutureSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {
                        tellSuccess(sender, Lang.get(_SUCCESS, serverName));
                    }
                });
    }
}
