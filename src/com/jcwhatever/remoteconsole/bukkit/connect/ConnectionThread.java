package com.jcwhatever.remoteconsole.bukkit.connect;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.Utils;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.remoteconsole.bukkit.RemoteConsolePlugin;
import com.jcwhatever.remoteconsole.data.ConsoleCommand;
import com.jcwhatever.remoteconsole.data.LogLine;
import com.jcwhatever.remoteconsole.data.LogText;
import com.jcwhatever.remoteconsole.data.ServerClosed;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;

/**
 * A {@link Runnable} responsible for sending output and receiving commands from a single
 * console viewer.
 */
public class ConnectionThread implements Runnable {

    private final ServerInfo _connection;
    private final ConcurrentLinkedQueue<Object> _queue = new ConcurrentLinkedQueue<>();

    private Client _client;
    private Thread _thread;
    private int _tries;
    private FutureResultAgent<ConnectionThread> _connectAgent;
    private FutureResultAgent<ConnectionThread> _disconnectAgent;
    private volatile long _disconnectTime;
    private volatile long _connectTime;

    /**
     * Constructor.
     *
     * @param connection  The connection info.
     */
    ConnectionThread(ServerInfo connection) {
        PreCon.notNull(connection);

        _connection = connection;

        initClient();

        _thread = new Thread(this);
    }

    /**
     * Get the out queue.
     *
     * <p>This is the queue that is checked by the thread for items to send
     * to its viewer.</p>
     */
    public Queue<Object> getOutQueue() {
        return _queue;
    }

    /**
     * Get the thread.
     */
    public Thread getThread() {
        return _thread;
    }

    /**
     * Determine if the client is connected.
     */
    public boolean isConnected() {
        return _client.isConnected();
    }

    /**
     * Get the time that the last connection was established. Returns 0
     * if never connected.
     */
    public long getConnectTime() {
        return _connectTime;
    }

    /**
     * Get the time that the connection was disconnected. Returns 0
     * if never connected or disconnected.
     */
    public long getDisconnectTime() {
        return _disconnectTime;
    }

    /**
     * Determine if the thread is running.
     */
    public boolean isRunning() {
        return _thread.isAlive();
    }

    public void connect(@Nullable FutureResultAgent<ConnectionThread> agent) {

        _connectAgent = agent;

        if (_client.isConnected()) {

            if (agent != null)
                agent.success(this);

            return;
        }

        if (_thread.isAlive()) {
            _tries = 0;
        } else {
            initClient();

            _thread = new Thread(this);
            _thread.start();
        }
    }

    public void disconnect(@Nullable FutureResultAgent<ConnectionThread> agent) {

        _disconnectAgent = agent;

        if (!_client.isConnected()) {

            if (agent != null)
                agent.success(this);

            return;
        }

        _thread.interrupt();
    }

    @Override
    public void run() {

        _client.start();
        _client.addListener(new Listener() {

            @Override
            public void received(Connection connection, Object received) {

                if (received instanceof ConsoleCommand) {

                    final ConsoleCommand cmd = (ConsoleCommand) received;

                    if (cmd.command == null)
                        return;

                    out("Executing command as console from " +
                            connection + ' ' + connection.getRemoteAddressTCP() + ": " +
                            cmd.command);

                    Scheduler.runTaskSync(RemoteConsolePlugin.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            Utils.executeAsConsole(cmd.command);
                        }
                    });

                }
                else if (received instanceof ServerClosed) {

                    out("Remote console at " + connection + ' ' + connection.getRemoteAddressTCP() + " disconnected.");

                    threadDisconnect(connection);
                }
            }
        });

        // connect to console viewer
        if (!threadConnect())
            return;

        while (true) {

            // send queued text to viewers
            while (!_queue.isEmpty()) {

                Object message = _queue.remove();
                _client.sendTCP(message);

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
            }

            // sleep for 1 second
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }

            // make sure still connected
            if (!_client.isConnected()) {
                _disconnectTime = System.currentTimeMillis();

                if (!threadConnect())
                    break;
            }
        }

        if (_disconnectAgent != null) {
            _disconnectAgent.success(this);
            _disconnectAgent = null;
        }

        threadDisconnect(null);
    }

    /**
     * Establish a connection to the server.
     *
     * <p>Failed connections are retried up to 5 times every 2.5 seconds.</p>
     */
    private boolean threadConnect() {

        while (_tries < 5) {
            try {

                out("Connecting to remote console viewer: " +
                        _connection.getAddress() + ':' + _connection.getPort() +
                        " Attempt " + (_tries + 1) + " of 5.");

                _client.connect(_connection.getTimeout(), _connection.getAddress(),
                        _connection.getPort());

                out("Connected to remote console viewer.");

                _connectTime = System.currentTimeMillis();

                if (_connectAgent != null) {
                    _connectAgent.success(this);
                    _connectAgent = null;
                }

                return true;

            } catch (IOException e) {

                err("Failed to connect to remote console viewer: " +
                        _connection.getAddress() + ':' + _connection.getPort());

                if (_tries >= 4) {
                    e.printStackTrace();

                    if (_connectAgent != null) {
                        _connectAgent.error(this);
                        _connectAgent = null;
                    }
                }
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                break;
            }

            _tries++;
        }

        _tries = 0;
        threadDisconnect(null);
        return false;
    }

    private void threadDisconnect(@Nullable Connection connection) {

        _disconnectTime = System.currentTimeMillis();

        if (connection != null)
            connection.close();

        _client.stop();
        _client.close();
        _thread.interrupt();
    }

    private void initClient() {
        _client = new Client();
        _client.setName(_connection.getName());

        // register serializable output objects
        Kryo kryo = _client.getKryo();
        kryo.register(LogText.class);
        kryo.register(LogLine.class);
        kryo.register(ConsoleCommand.class);
        kryo.register(ServerClosed.class);
    }

    private void out(String message) {

        String time = RemoteConsolePlugin.formatTime(System.currentTimeMillis());

        System.out.println('[' + time + "] " + RemoteConsolePlugin.getPlugin().getConsolePrefix() + message);
    }

    private void err(String message) {

        String time = RemoteConsolePlugin.formatTime(System.currentTimeMillis());

        System.err.println('[' + time + "] " + RemoteConsolePlugin.getPlugin().getConsolePrefix() + message);
    }
}
