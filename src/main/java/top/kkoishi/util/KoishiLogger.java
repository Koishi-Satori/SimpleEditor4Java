package top.kkoishi.util;

import top.kkoishi.io.Files;

import java.io.*;
import java.lang.System.LoggerFinder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public abstract class KoishiLogger extends PrintStream implements System.Logger {

    public static volatile boolean toFile = false;

    public static volatile String errFile;

    public static volatile String outFile;

    public static volatile boolean override;

    /**
     * An implementation of KoishiLogger.
     */
    private static final class KoishiLoggerImpl extends KoishiLogger {

        /**
         * The logger date format.
         */
        private static final SimpleDateFormat FORMAT = new SimpleDateFormat("[yyyy-MM-dd hh:mm:ss]");

        /**
         * Default constructor of KoishiLoggerImpl.
         *
         * @param out output stream.
         * @param standard the standard level.
         */
        private KoishiLoggerImpl (OutputStream out, Level standard) {
            this(out);
            super.standard = standard;
        }

        /**
         * Creates a new print stream, without automatic line flushing, with the
         * specified OutputStream. Characters written to the stream are converted
         * to bytes using the platform's default character encoding.
         *
         * @param out The output stream to which values and objects will be
         *            printed
         * @see PrintWriter#PrintWriter(OutputStream)
         */
        private KoishiLoggerImpl (OutputStream out) {
            super(out);
        }

        /**
         * Get the log time.
         *
         * @return formatted time.
         */
        @Override
        public synchronized String getLoggerTime () {
            return FORMAT.format(new Date(System.currentTimeMillis()));
        }
    }

    protected Level standard = Level.ALL;

    public synchronized static KoishiLogger getInstance() {
        return getInstance(System.out);
    }

    public synchronized static KoishiLogger getInstance (OutputStream os) {
        return getInstance(Level.ALL, os);
    }

    public boolean useErr = false;

    public synchronized static KoishiLogger getInstance (Level logLevel, OutputStream os) {
        return new KoishiLoggerImpl(os, logLevel);
    }

    public static void main (String[] args) {
        System.setOut(getInstance());
        System.out.println("Fuck LZU");
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified OutputStream. Characters written to the stream are converted
     * to bytes using the platform's default character encoding.
     *
     * @param out The output stream to which values and objects will be
     *            printed
     * @see PrintWriter#PrintWriter(OutputStream)
     */
    protected KoishiLogger (OutputStream out) {
        super(out);
    }

    /**
     * Creates a new print stream, with the specified OutputStream, line
     * flushing and charset.  This convenience constructor creates the necessary
     * intermediate {@link OutputStreamWriter OutputStreamWriter},
     * which will encode characters using the provided charset.
     *
     * @param out       The output stream to which values and objects will be
     *                  printed
     * @param autoFlush Whether the output buffer will be flushed
     *                  whenever a byte array is written, one of the
     *                  {@code println} methods is invoked, or a newline
     *                  character or byte ({@code '\n'}) is written
     * @param charset   A {@linkplain Charset charset}
     * @since 10
     */
    protected KoishiLogger (OutputStream out, boolean autoFlush, Charset charset) {
        super(out, autoFlush, charset);
    }

    /**
     * Returns the name of this logger.
     *
     * @return the logger name.
     */
    @Override
    public synchronized String getName () {
        if (!useErr) {
            return "[KKoishi_OutputLogger@LogLevel:" + standard.getName() + ']';
        }
        return "[KKoishi_ErrorLogger@LogLevel:" + standard.getName() + ']';
    }

    /**
     * Checks if a message of the given level would be logged by
     * this logger.
     *
     * @param level the log message level.
     * @return {@code true} if the given log message level is currently
     * being logged.
     * @throws NullPointerException if {@code level} is {@code null}.
     */
    @Override
    public synchronized boolean isLoggable (Level level) {
        return level.getSeverity() >= standard.getSeverity();
    }

    /**
     * Logs a message.
     *
     * @param level the log message level.
     * @param msg   the string message (or a key in the message catalog, if
     *              this logger is a {@link
     *              LoggerFinder#getLocalizedLogger(String,
     *              ResourceBundle, Module) localized logger});
     *              can be {@code null}.
     * @throws NullPointerException if {@code level} is {@code null}.
     * @implSpec The default implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, msg, (Object[])null);}
     */
    @Override
    public synchronized void log (Level level, String msg) {
        System.Logger.super.log(level, msg);
    }

    /**
     * Logs a lazily supplied message.
     * <p>
     * If the logger is currently enabled for the given log message level
     * then a message is logged that is the result produced by the
     * given supplier function.  Otherwise, the supplier is not operated on.
     *
     * @param level       the log message level.
     * @param msgSupplier a supplier function that produces a message.
     * @throws NullPointerException if {@code level} is {@code null},
     *                              or {@code msgSupplier} is {@code null}.
     * @implSpec When logging is enabled for the given level, the default
     * implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, msgSupplier.get(), (Object[])null);}
     */
    @Override
    public synchronized void log (Level level, Supplier<String> msgSupplier) {
        System.Logger.super.log(level, msgSupplier);
    }

    /**
     * Logs a message produced from the given object.
     * <p>
     * If the logger is currently enabled for the given log message level then
     * a message is logged that, by default, is the result produced from
     * calling  toString on the given object.
     * Otherwise, the object is not operated on.
     *
     * @param level the log message level.
     * @param obj   the object to log.
     * @throws NullPointerException if {@code level} is {@code null}, or
     *                              {@code obj} is {@code null}.
     * @implSpec When logging is enabled for the given level, the default
     * implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, obj.toString(), (Object[])null);}
     */
    @Override
    public synchronized void log (Level level, Object obj) {
        System.Logger.super.log(level, obj);
    }

    /**
     * Logs a message associated with a given throwable.
     *
     * @param level  the log message level.
     * @param msg    the string message (or a key in the message catalog, if
     *               this logger is a {@link
     *               LoggerFinder#getLocalizedLogger(String,
     *               ResourceBundle, Module) localized logger});
     *               can be {@code null}.
     * @param thrown a {@code Throwable} associated with the log message;
     *               can be {@code null}.
     * @throws NullPointerException if {@code level} is {@code null}.
     * @implSpec The default implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, msg, thrown);}
     */
    @Override
    public synchronized void log (Level level, String msg, Throwable thrown) {
        System.Logger.super.log(level, msg, thrown);
    }

    /**
     * Logs a lazily supplied message associated with a given throwable.
     * <p>
     * If the logger is currently enabled for the given log message level
     * then a message is logged that is the result produced by the
     * given supplier function.  Otherwise, the supplier is not operated on.
     *
     * @param level       one of the log message level identifiers.
     * @param msgSupplier a supplier function that produces a message.
     * @param thrown      a {@code Throwable} associated with log message;
     *                    can be {@code null}.
     * @throws NullPointerException if {@code level} is {@code null}, or
     *                              {@code msgSupplier} is {@code null}.
     * @implSpec When logging is enabled for the given level, the default
     * implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, msgSupplier.get(), thrown);}
     */
    @Override
    public synchronized void log (Level level, Supplier<String> msgSupplier, Throwable thrown) {
        System.Logger.super.log(level, msgSupplier, thrown);
    }

    /**
     * Logs a message with an optional list of parameters.
     *
     * @param level  one of the log message level identifiers.
     * @param format the string message format in {@link
     *               MessageFormat} format, (or a key in the message
     *               catalog, if this logger is a {@link
     *               LoggerFinder#getLocalizedLogger(String,
     *               ResourceBundle, Module) localized logger});
     *               can be {@code null}.
     * @param params an optional list of parameters to the message (may be
     *               none).
     * @throws NullPointerException if {@code level} is {@code null}.
     * @implSpec The default implementation for this method calls
     * {@code this.log(level, (ResourceBundle)null, format, params);}
     */
    @Override
    public synchronized void log (Level level, String format, Object... params) {
        System.Logger.super.log(level, format, params);
    }

    /**
     * Logs a localized message associated with a given throwable.
     * <p>
     * If the given resource bundle is non-{@code null},  the {@code msg}
     * string is localized using the given resource bundle.
     * Otherwise the {@code msg} string is not localized.
     *
     * @param level  the log message level.
     * @param bundle a resource bundle to localize {@code msg}; can be
     *               {@code null}.
     * @param msg    the string message (or a key in the message catalog,
     *               if {@code bundle} is not {@code null}); can be {@code null}.
     * @param thrown a {@code Throwable} associated with the log message;
     *               can be {@code null}.
     * @throws NullPointerException if {@code level} is {@code null}.
     */
    @Override
    public synchronized void log (Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        if (isLoggable(level)) {
            println(msg + "|Caused by:");
            println(thrown);
        }
    }

    /**
     * Logs a message with resource bundle and an optional list of
     * parameters.
     * <p>
     * If the given resource bundle is non-{@code null},  the {@code format}
     * string is localized using the given resource bundle.
     * Otherwise the {@code format} string is not localized.
     *
     * @param level  the log message level.
     * @param bundle a resource bundle to localize {@code format}; can be
     *               {@code null}.
     * @param format the string message format in {@link
     *               MessageFormat} format, (or a key in the message
     *               catalog if {@code bundle} is not {@code null}); can be {@code null}.
     * @param params an optional list of parameters to the message (may be
     *               none).
     * @throws NullPointerException if {@code level} is {@code null}.
     */
    @Override
    public synchronized void log (Level level, ResourceBundle bundle, String format, Object... params) {
        if (isLoggable(level)) {
            printf(bundle.getLocale(), (getLoggerTime() + format), (Object[]) params);
        }
    }

    /**
     * Prints a String and then terminate the line.  This method behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x The {@code String} to be printed.
     */
    @Override
    public void println (String x) {
        super.println(getName() + getLoggerTime() + x);
        if (toFile) {
            if (override) {
                try {
                    Files.write(useErr ? errFile : outFile ,getName() + getLoggerTime() + x + '\n');
                } catch (IOException e) {
                }
            } else {
                try {
                    Files.append(useErr ? errFile : outFile, getName() + getLoggerTime() + x + '\n');
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Prints an Object and then terminate the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x The {@code Object} to be printed.
     */
    @Override
    public void println (Object x) {
        super.println(x);
        if (toFile) {
            if (override) {
                try {
                    Files.write(useErr ? errFile : outFile ,getName() + getLoggerTime() + x + '\n');
                } catch (IOException e) {
                }
            } else {
                try {
                    Files.append(useErr ? errFile : outFile, getName() + getLoggerTime() + x + '\n');
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * A convenience method to write a formatted string to this output stream
     * using the specified format string and arguments.
     *
     * <p> An invocation of this method of the form
     * {@code out.printf(l, format, args)} behaves
     * in exactly the same way as the invocation
     *
     * <pre>{@code
     *     out.format(l, format, args)
     * }</pre>
     *
     * @param l      The {@linkplain Locale locale} to apply during
     *               formatting.  If {@code l} is {@code null} then no localization
     *               is applied.
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               {@code null} argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     * @return This output stream
     * @throws IllegalFormatException If a format string contains an illegal syntax, a format
     *                                specifier that is incompatible with the given arguments,
     *                                insufficient arguments given the format string, or other
     *                                illegal conditions.  For specification of all possible
     *                                formatting errors, see the <a
     *                                href="../util/Formatter.html#detail">Details</a> section of the
     *                                formatter class specification.
     * @throws NullPointerException   If the {@code format} is {@code null}
     * @since 1.5
     */
    @Override
    public PrintStream printf (Locale l, String format, Object... args) {
        if (toFile) {
            if (override) {
                try {
                    Files.write(useErr ? errFile : outFile ,(getName() + getLoggerTime() + format).formatted(args));
                } catch (IOException e) {
                }
            } else {
                try {
                    Files.append(useErr ? errFile : outFile, (getName() + getLoggerTime() + format).formatted(args));
                } catch (IOException e) {
                }
            }
        }
        return super.printf(l, getName() + getLoggerTime() + format, args);
    }

    /**
     * Get the log time.
     *
     * @return formatted time.
     */
    protected abstract String getLoggerTime ();
}
