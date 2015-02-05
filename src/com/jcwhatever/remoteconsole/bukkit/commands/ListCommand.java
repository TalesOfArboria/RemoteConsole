package com.jcwhatever.remoteconsole.bukkit.commands;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.remoteconsole.bukkit.Lang;
import com.jcwhatever.remoteconsole.bukkit.Msg;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;
import com.jcwhatever.remoteconsole.bukkit.connect.ServerInfo;

import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        command="list",
        staticParams={"page=1"},
        description="List all stored remote console servers.",
        paramDescriptions = {
                "page= {PAGE}"
        })
public class ListCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Remote Console Servers";
    @Localizable static final String _LABEL_CONNECTED = "{GREEN}Connected";
    @Localizable static final String _LABEL_DISCONNECTED = "{RED}Disconnected";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        Collection<ServerInfo> connections = manager.getAll();

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        String connectedLabel = Lang.get(_LABEL_CONNECTED);
        String disconnectedLabel = Lang.get(_LABEL_DISCONNECTED);
        for (ServerInfo connection : connections) {
            pagin.add(connection.getName(), manager.isConnected(connection.getName())
                    ? connectedLabel
                    : disconnectedLabel);
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}