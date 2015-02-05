package com.jcwhatever.remoteconsole.bukkit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Utils;
import com.jcwhatever.remoteconsole.data.ConsoleCommand;
import com.jcwhatever.remoteconsole.data.LogLine;
import com.jcwhatever.remoteconsole.data.LogText;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A {@link Runnable} responsible for sending output and receiving commands from a single
 * console viewer.
 */
public class ConnectionThread implements Runnable {

    private final ServerConnection _connection;
    private final ConcurrentLinkedQueue<Object> _queue = new ConcurrentLinkedQueue<>();
    private final Client _client = new Client();
    private final Thread _thread;

    private int tries;

    /**
     * Constructor.
     *
     * @param connection  The connection info.
     */
    ConnectionThread(ServerConnection connection) {
        PreCon.notNull(connection);

        _connection = connection;

        _client.setName(connection.getName());

        // register serializable output objects
        Kryo kryo = _client.getKryo();
        kryo.register(LogText.class);
        kryo.register(LogLine.class);
        kryo.register(ConsoleCommand.class);

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

    @Override
    public void run() {

        _client.start();
        _client.addListener(new Listener() {

            @Override
            public void received(Connection connection, Object received) {

                if (received instanceof ConsoleCommand) {

                    System.out.println("Executing command as console from " +
                            connection + ' ' + connection.getRemoteAddressTCP() + ": " +
                            ((ConsoleCommand) received).command);

                    Utils.executeAsConsole(((ConsoleCommand) received).command);
                }
            }
        });

        // connect to console viewer
        if (!connect())
            return;

        while (true) {

            // send queued text to viewers
            while (!_queue.isEmpty()) {
                Object message = _queue.remove();
                _client.sendTCP(message);
            }

            // sleep for 1 second.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }

            // make sure still connected
            if (!_client.isConnected() && !connect()) {
                break;
            }
        }

        _client.stop();
        _client.close();
    }

    /**
     * Establish a connection to the server.
     *
     * <p>Failed connections are retried up to 5 times every 2.5 seconds.</p>
     */
    private boolean connect() {

        while (tries < 5) {
            try {

                System.out.println("Connecting to remote console viewer: " +
                        _connection.getAddress() + ':' + _connection.getPort() +
                        " Attempt " + (tries + 1) + " of 5.");

                _client.connect(_connection.getTimeout(), _connection.getAddress(),
                        _connection.getPort());

                System.out.println("Connected to remote console viewer.");

                return true;

            } catch (IOException e) {
                System.err.println("Failed to connect to remote console viewer: " +
                        _connection.getAddress() + ':' + _connection.getPort());

                if (tries >= 4)
                    e.printStackTrace();
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                break;
            }

            tries++;
        }

        tries = 0;
        _client.stop();
        _client.close();
        return false;
    }
}
