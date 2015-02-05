package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.NucleusPlugin;

import org.bukkit.Bukkit;

import java.util.logging.Handler;

/**
 * Remote Console Bukkit plugin.
 */
public class RemoteConsolePlugin extends NucleusPlugin {

    private static RemoteConsolePlugin _instance;

    private ConnectionManager _manager;
    private LogPrintStream _outStream;
    private LogPrintStream _errStream;
    private Handler _logHandler;

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

    @Override
    public String getChatPrefix() {
        return "[RemLogger]";
    }

    @Override
    public String getConsolePrefix() {
        return "[RemoteLogger]";
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
