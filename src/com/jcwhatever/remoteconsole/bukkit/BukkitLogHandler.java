package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.utils.PreCon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Bukkit logger handler.
 */
public class BukkitLogHandler  extends Handler {

    private final ConnectionManager _manager;
    private final SimpleDateFormat _dateFormatter = new SimpleDateFormat("HH:mm:ss");

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

        Date date = new Date(record.getMillis());

        String time = _dateFormatter.format(date);

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