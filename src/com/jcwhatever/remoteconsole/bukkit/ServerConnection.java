package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Storage container for a console viewer connection info.
 */
public class ServerConnection {

    private final String _name;
    private final IDataNode _dataNode;
    private int _timeout = 5000;
    private String _serverAddress;
    private int _port = 54555;

    /**
     * Constructor.
     *
     * @param dataNode  The data node that stores the server info.
     */
    public ServerConnection(String name, IDataNode dataNode) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(dataNode);

        _name = name;
        _dataNode = dataNode;
        _timeout = dataNode.getInteger("timeout", _timeout);
        _serverAddress = dataNode.getString("address");
        _port = dataNode.getInteger("port", _port);
    }

    /**
     * Get the viewer name.
     */
    public String getName() {
        return _name;
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
}
