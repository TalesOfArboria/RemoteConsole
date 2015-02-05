package com.jcwhatever.remoteconsole.bukkit.connect;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;
import com.jcwhatever.remoteconsole.data.LogLine;
import com.jcwhatever.remoteconsole.data.LogText;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages connections to remote console viewers.
 */
public class ConnectionManager extends NamedInsensitiveDataManager<ServerInfo> implements IDisposable {

    private final Map<ServerInfo, ConnectionThread> _threadMap = new HashMap<>(5);

    private volatile boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public ConnectionManager(IDataNode dataNode) {
        super(dataNode, false);

        PreCon.notNull(dataNode);

        load();
    }

    /**
     * Add a new remote console server connection.
     *
     * @param name     The unique name of the server.
     * @param address  The server address.
     * @param port     The server listening port.
     *
     * @return  The new {@code ServerConnection} or null if the name already exists.
     */
    public ServerInfo add(String name, String address, int port) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(address);
        PreCon.positiveNumber(port);

        if (contains(name))
            return null;

        IDataNode node = getNode(name);

        ServerInfo connection = new ServerInfo(name, node);
        connection.setAddress(address);
        connection.setPort(port);

        add(connection);

        return connection;
    }

    /**
     * Determine if there is an established connection to the
     * specified remote console server.
     *
     * @param name  The name of the remote console server.
     */
    public boolean isConnected(String name) {

        ServerInfo connection = get(name);
        if (connection == null)
            return false;

        ConnectionThread thread = _threadMap.get(connection);
        return thread != null && thread.isConnected();
    }

    /**
     * Connect to a remote console server.
     *
     * @param name  The name of the server.
     *
     * @return  A future whose possible results are success or error.
     */
    public Future<ConnectionThread> connect(String name) {
        PreCon.notNull(name);

        FutureResultAgent<ConnectionThread> agent = new FutureResultAgent<>();

        ServerInfo connection = get(name);
        if (connection == null)
            return agent.error();

        ConnectionThread thread = _threadMap.get(connection);
        if (thread == null)
            return agent.error();

        if (thread.isConnected())
            return agent.success(thread);

        thread.connect(agent);

        return agent.getFuture();
    }

    /**
     * Disconnect from a remote console server.
     *
     * @param name  The name of the server.
     *
     * @return A future whose possible results are success or error.
     */
    public Future<ConnectionThread> disconnect(String name) {
        PreCon.notNull(name);

        FutureResultAgent<ConnectionThread> agent = new FutureResultAgent<>();

        ServerInfo connection = get(name);
        if (connection == null)
            return agent.error();

        ConnectionThread thread = _threadMap.get(connection);
        if (thread == null)
            return agent.error();

        if (!thread.isConnected())
            return agent.success(thread);

        thread.disconnect(agent);

        return agent.getFuture();
    }

    /**
     * Send text to all remote viewers.
     *
     * @param text  The text to send.
     */
    public void sendText(String text) {

        if (_isDisposed)
            return;

        addOutQueue(new LogText(text));
    }

    /**
     * Send a line of text to all remote viewers.
     *
     * @param line  The line of text to send.
     */
    public void sendLine(String line) {

        if (_isDisposed)
            return;

        addOutQueue(new LogLine(line));
    }

    @Nullable
    @Override
    protected ServerInfo load(String name, IDataNode serverNode) {

        ServerInfo connection = new ServerInfo(serverNode.getName(), serverNode);
        ConnectionThread thread = new ConnectionThread(connection);
        _threadMap.put(connection, thread);

        if (connection.isStartupConnect())
            thread.connect(null);

        return connection;
    }

    @Override
    protected void save(ServerInfo item, IDataNode itemNode) {
        // do nothing, ServerConnection object handles saving itself.
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

        for (ConnectionThread connection : _threadMap.values()) {
            connection.getThread().interrupt();
        }

        _threadMap.clear();
    }

    /**
     * Send an object to all connections.
     *
     * @param outgoing  The object to send.
     */
    private void addOutQueue(Object outgoing) {
        for (ConnectionThread connection : _threadMap.values()) {

            if (!connection.isConnected()) {

                if (connection.getDisconnectTime() < System.currentTimeMillis() - 5000)
                    continue;
            }

            connection.getOutQueue().add(outgoing);
        }
    }
}
