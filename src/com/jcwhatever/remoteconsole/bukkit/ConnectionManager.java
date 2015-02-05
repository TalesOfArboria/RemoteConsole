package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.remoteconsole.data.LogLine;
import com.jcwhatever.remoteconsole.data.LogText;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages connections to remote console viewers.
 */
public class ConnectionManager implements IDisposable {

    private final IDataNode _dataNode;
    private final List<ConnectionThread> _connections = new ArrayList<>(5);

    private volatile boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public ConnectionManager(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;

        load();
    }

    /**
     * Send text to all remote viewers.
     *
     * @param text  The text to send.
     */
    public void sendText(String text) {

        if (_isDisposed)
            return;

        LogText logText = new LogText(text);

        for (ConnectionThread connection : _connections) {
            connection.getOutQueue().add(logText);
        }
    }

    /**
     * Send a line of text to all remote viewers.
     *
     * @param line  The line of text to send.
     */
    public void sendLine(String line) {

        if (_isDisposed)
            return;

        LogLine logLine = new LogLine(line);

        for (ConnectionThread connection : _connections) {
            connection.getOutQueue().add(logLine);
        }
    }

    /**
     * Load server connection settings
     */
    private void load() {
        for (IDataNode serverNode : _dataNode) {

            ServerConnection connection = new ServerConnection(serverNode.getName(), serverNode);
            ConnectionThread thread = new ConnectionThread(connection);
            _connections.add(thread);

            thread.getThread().start();
        }
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        // give time for final messages to be sent
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}

        _isDisposed = true;

        for (ConnectionThread connection : _connections) {
            connection.getThread().interrupt();
        }

        _connections.clear();
    }
}
