package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Bukkit logger handler.
 */
public class BukkitLogHandler  extends Handler {

    private final ConnectionManager _manager;


    /**
     * Constructor.
     *
     * @param manager  The current connection manager.
     */
    BukkitLogHandler(ConnectionManager manager) {
        PreCon.notNull(manager);

        _manager = manager;
    }

    @Override
    public void publish(LogRecord record) {

        String time = RemoteConsolePlugin.formatTime(record.getMillis());

        String line = '[' + time + "] [" + record.getLevel().getName() + "] " + record.getMessage();

        _manager.sendLine(line);

        // ensure exception stack traces are sent
        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable throwable = record.getThrown();
        if (throwable != null) {
            StringWriter stringWriter = new StringWriter(100);
            PrintWriter writer = new PrintWriter(stringWriter);
            throwable.printStackTrace(writer);

            _manager.sendLine(stringWriter.toString());
        }
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void close() throws SecurityException {
        // do nothing
    }
}