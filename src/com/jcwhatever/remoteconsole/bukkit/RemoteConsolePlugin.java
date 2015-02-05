package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.remoteconsole.bukkit.commands.Dispatcher;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;

import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;

/**
 * Remote Console Bukkit plugin.
 */
public class RemoteConsolePlugin extends NucleusPlugin {

    private static RemoteConsolePlugin _instance;

    private final SimpleDateFormat _dateFormatter = new SimpleDateFormat("HH:mm:ss");

    private ConnectionManager _manager;
    private LogPrintStream _outStream;
    private LogPrintStream _errStream;
    private Handler _logHandler;

    private final String CHAT_PREFIX =
            TextColor.WHITE + "[" + TextColor.YELLOW + "RCon" + TextColor.WHITE + "] ";

    /**
     * Get the current plugin instance.
     */
    public static RemoteConsolePlugin getPlugin() {
        return _instance;
    }

    /**
     * Get the connection manager.
     */
    public static ConnectionManager getConnectionManager() {
        return _instance._manager;
    }

    /**
     * Format time into a string.
     *
     * @param time  The time to format.
     */
    public static String formatTime(long time) {
        Date date = new Date(time);

        return _instance._dateFormatter.format(date);
    }

    @Override
    public String getChatPrefix() {
        return CHAT_PREFIX;
    }

    @Override
    public String getConsolePrefix() {
        return "[RemoteConsole] ";
    }

    @Override
    protected void onInit() {
        _instance = this;
    }

    @Override
    protected void onEnablePlugin() {

        _instance = this;

        _manager = new ConnectionManager(getDataNode().getNode("servers"));

        _outStream = new LogPrintStream(System.out);
        System.setOut(_outStream);

        _errStream = new LogPrintStream(System.err);
        System.setErr(_errStream);

        _logHandler = new BukkitLogHandler(_manager);
        Bukkit.getLogger().addHandler(_logHandler);

        registerCommands(new Dispatcher(this));
    }

    @Override
    protected void onDisablePlugin() {

        Bukkit.getLogger().removeHandler(_logHandler);
        _manager.dispose();

        System.setOut(_outStream.getStream());
        System.setErr(_errStream.getStream());

        _instance = null;
    }
}
