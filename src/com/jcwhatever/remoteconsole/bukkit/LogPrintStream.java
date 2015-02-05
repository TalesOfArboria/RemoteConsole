package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Locale;

/**
 * A wrapper for the current {@code System.out} or {@code System.err} {@link PrintStream}.
 * Intended to replace.
 */
public class LogPrintStream extends PrintStream {

    private static final String LINE_SEPARATOR = "\n";

    private final PrintStream _stream;
    private Formatter _formatter;
    private StringBuilder _sbBuffer = new StringBuilder(10);

    /**
     * Constructor.
     *
     * @param out  The {@code PrintStream} to wrap.
     */
    public LogPrintStream(PrintStream out) {
        super(new ByteArrayOutputStream());

        _stream = out;
    }

    /**
     * Get the wrapped {@code PrintStream}.
     */
    public PrintStream getStream() {
        return _stream;
    }

    @Override
    public void flush() {
        _stream.flush();
    }

    @Override
    public boolean checkError() {
        return _stream.checkError();
    }

    @Override
    public void write(int b) {
        _stream.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String remoteString = new String(buf.clone(), off, len);
        connection().sendText(remoteString);

        if (remoteString.endsWith(LINE_SEPARATOR)) {
            remoteString = remoteString.substring(0, remoteString.length() - 1);

            int origLen = buf.length;
            buf = remoteString.getBytes();
            len = len == origLen ? buf.length : len;
        }

        _stream.write(buf, off, len);
    }

    @Override
    public void print(boolean b) {
        writeString(b ? "true" : "false");
    }

    @Override
    public void print(char c) {
        writeString(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        writeString(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        writeString(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        writeString(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        writeString(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        writeString(new String(s));
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        writeString(s);
    }

    @Override
    public void print(Object obj) {
        writeString(String.valueOf(obj));
    }

    /* Methods that do terminate lines */

    @Override
    public void println() {
        writeString(LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(boolean x) {
        super.println(x);
        writeString((x ? "true" : "false") + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(char x) {
        writeString(String.valueOf(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(int x) {
        writeString(String.valueOf(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(long x) {
        writeString(String.valueOf(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(float x) {
        writeString(String.valueOf(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(double x) {
        writeString(String.valueOf(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(char[] x) {
        writeString(new String(x) + LINE_SEPARATOR);
    }

    @Override
    public synchronized void println(String x) {
        writeString((x == null ? "null" : x) + LINE_SEPARATOR);
    }

    @Override
    public void println(Object x) {
        writeString((x == null ? "null" : String.valueOf(x)) + LINE_SEPARATOR);
    }

    @Override
    public PrintStream printf(String format, Object ... args) {
        return format(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object ... args) {
        return format(l, format, args);
    }

    @Override
    public synchronized LogPrintStream format(String format, Object ... args) {
        if ((_formatter == null)
                || (_formatter.locale() != Locale.getDefault()))
            _formatter = new Formatter(_sbBuffer);

        _formatter.format(Locale.getDefault(), format, args);

        writeString(_sbBuffer.toString());

        return this;
    }

    @Override
    public synchronized LogPrintStream format(Locale l, String format, Object ... args) {
        if ((_formatter == null)
                || (_formatter.locale() != l)) {
            _formatter = new Formatter(_sbBuffer, l);
        }

        _formatter.format(l, format, args);

        writeString(_sbBuffer.toString());

        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        writeString(cs.subSequence(start, end).toString());
        return this;
    }

    private void writeString(String string) {
        byte[] bytes = string.getBytes();
        write(bytes, 0, bytes.length);
    }

    private ConnectionManager connection() {
        return RemoteConsolePlugin.getConnectionManager();
    }
}
