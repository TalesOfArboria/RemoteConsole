package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import com.jcwhatever.nucleus.utils.observer.future.Result;
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
public class ConnectCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _SERVER_NOT_FOUND =
            "A remote console server named '{0: server name}' was not found.";

    @Localizable static final String _FAILED =
            "Failed to connect to remote console server.";

    @Localizable static final String _SUCCESS =
            "Connected to remote console server '{0: server name}'.";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final String serverName = args.getName("serverName");

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        IFutureResult<ConnectionThread> result = manager.connect(serverName);

        result
                .onError(new FutureResultSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {

                        if (result.hasResult())
                            tellError(sender, Lang.get(_FAILED, serverName));
                        else
                            tellError(sender, Lang.get(_SERVER_NOT_FOUND, serverName));
                    }
                })
                .onSuccess(new FutureResultSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {
                        tellSuccess(sender, Lang.get(_SUCCESS, serverName));
                    }
                });
    }
}
