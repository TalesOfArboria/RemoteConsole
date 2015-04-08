package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
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
        command="disconnect",
        staticParams="serverName",
        description="Disconnect from a remote console.",
        paramDescriptions = {
                "serverName= The name of the remote console server."
        })
public class DisconnectCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _SERVER_NOT_FOUND =
            "A remote console server named '{0: server name}' was not found.";

    @Localizable static final String _SUCCESS =
            "Disconnected from remote console server '{0: server name}'.";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final String serverName = args.getName("serverName");

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        Future<ConnectionThread> result = manager.disconnect(serverName);

        result
                .onError(new FutureSubscriber<ConnectionThread>() {
                    @Override
                    public void on(Result<ConnectionThread> result) {
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
