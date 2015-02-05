package com.jcwhatever.remoteconsole;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.jcwhatever.remoteconsole.data.ConsoleCommand;
import com.jcwhatever.remoteconsole.data.LogLine;
import com.jcwhatever.remoteconsole.data.LogText;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Console server.
 */
public class Main {

    static Options _options = new Options();

    static {
        _options.addOption("port", true, "The port number to listen on. Default is 54555.");
        _options.addOption("help", false, "Get help.");
    }

    public static void main(String[] args) throws ParseException {

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(_options, args);

        if (cmd.hasOption("help")) {
            showHelp();
            return;
        }

        int port = 54555;

        if (cmd.hasOption("port")) {
            String rawPort = cmd.getOptionValue("port");

            try {
                port = Integer.parseInt(rawPort);
            }
            catch (NumberFormatException e) {
                showHelp();
                System.err.println("Number is expected for option 'port'.");
                System.exit(-1);
            }
        }

        // start server
        Server server = new Server();
        server.start();

        // register serializable transport classes.
        Kryo kryo = server.getKryo();
        kryo.register(LogText.class);
        kryo.register(LogLine.class);
        kryo.register(ConsoleCommand.class);

        try {
            server.bind(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Listening on port " + port + '.');
        System.out.println("Press Ctrl+C to end the program.");

        final Map<Connection, Void> _connections = new WeakHashMap<>(3);

        // add listener
        server.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                System.out.println();
                System.out.println("#---[" + connection + ' ' + connection.getRemoteAddressTCP() +
                        " CONNECTED]-----------------------------#");
                System.out.println();

                _connections.put(connection, null);
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println();
                System.out.println("#---[" + connection +
                        " DISCONNECTED]--------------------------#");
                System.out.println();

                _connections.remove(connection);
            }

            @Override
            public void received (Connection connection, Object object) {
                if (object instanceof LogText) {

                    System.out.print(((LogText) object).text);
                }
                else if (object instanceof LogLine) {
                    System.out.println(((LogLine) object).text);
                }
            }
        });

        Console console = System.console();

        // keep program alive
        while (true) {

            if (console != null) {

                // allow sending commands back to server if console
                String input = console.readLine();

                for (Connection connection : _connections.keySet()) {
                    if (connection.isConnected()) {
                        connection.sendTCP(new ConsoleCommand(input));
                    }
                }
            }
            else {

                // no console, keep alive
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        server.stop();
        server.close();
    }

    /**
     * Show help.
     */
    private static void showHelp() {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar " + getJar().getName(), _options);
    }

    /**
     * Get the RemoteConsoleViewer jar file.
     */
    private static File getJar() {

        try {
            return new File(
                    Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
