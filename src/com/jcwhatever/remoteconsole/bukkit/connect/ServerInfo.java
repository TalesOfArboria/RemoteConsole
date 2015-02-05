package com.jcwhatever.remoteconsole.bukkit.connect;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Storage container for a console viewer connection info.
 */
public class ServerInfo implements INamedInsensitive {

    private final String _name;
    private final String _searchName;
    private final IDataNode _dataNode;
    private int _timeout = 5000;
    private String _serverAddress;
    private int _port = 54555;
    private boolean _isStartupConnect;

    /**
     * Constructor.
     *
     * @param dataNode  The data node that stores the server info.
     */
    public ServerInfo(String name, IDataNode dataNode) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(dataNode);

        _name = name;
        _searchName = name.toLowerCase();
        _dataNode = dataNode;
        _timeout = dataNode.getInteger("timeout", _timeout);
        _serverAddress = dataNode.getString("address");
        _port = dataNode.getInteger("port", _port);
        _isStartupConnect = dataNode.getBoolean("startup");
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Get the connection timeout in milliseconds.
     */
    public int getTimeout() {
        return _timeout;
    }

    /**
     * Set the connection timeout in milliseconds.
     *
     * @param timeout  The timeout.
     */
    public void setTimeout(int timeout) {
        _timeout = timeout;

        _dataNode.set("timeout", timeout);
        _dataNode.save();
    }

    /**
     * Get the console viewer address.
     */
    public String getAddress() {
        return _serverAddress;
    }

    /**
     * Set the console viewer address.
     *
     * @param address  The address.
     */
    public void setAddress(String address) {
        PreCon.notNullOrEmpty(address);

        _serverAddress = address;

        _dataNode.set("address", address);
        _dataNode.save();
    }

    /**
     * Get the console viewer connection port.
     */
    public int getPort() {
        return _port;
    }

    /**
     * Set the console viewer connection port.
     *
     * @param port  The port number.
     */
    public void setPort(int port) {
        _port = port;

        _dataNode.set("port", port);
        _dataNode.save();
    }

    /**
     * Determine if the connection should be established when the plugin is
     * first enabled.
     */
    public boolean isStartupConnect() {
        return _isStartupConnect;
    }

    /**
     * Set the servers startup connection status.
     *
     * @param isStartupConnect  True to connect on startup, otherwise false.
     */
    public void setStartupConnect(boolean isStartupConnect) {
        _isStartupConnect = isStartupConnect;

        _dataNode.set("startup", isStartupConnect);
        _dataNode.save();
    }
}
