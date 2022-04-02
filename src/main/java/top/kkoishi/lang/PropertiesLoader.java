package top.kkoishi.lang;

import java.nio.charset.Charset;
import java.security.PrivilegedActionException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author KKoishi_
 * @param <T> The target.
 */
public abstract class PropertiesLoader<T> {

    public static final List<Charset> CHARSET_LIST = new CopyOnWriteArrayList<>(Charset.availableCharsets().values());

    private final List<T> result = new LinkedList<>();

    protected String in;

    protected abstract T getInstance (Object ...params);

    protected abstract boolean hasNext ();

    protected abstract Object[] next () throws IllegalOrBadPropertyFormatException;

    public final void load (String in) {
        this.in = in;
    }

    public void translate () throws IllegalOrBadPropertyFormatException {
        result.clear();
        while (hasNext()) {
            result.add(getInstance(next()));
        }
    }

    public List<T> getResult () {
        return result;
    }

    /**
     * @author KKoishi_
     */
    public static class IllegalOrBadPropertyFormatException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public IllegalOrBadPropertyFormatException () {
        }

        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public IllegalOrBadPropertyFormatException (String message) {
            super(message);
        }

        /**
         * Constructs a new exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A {@code null} value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public IllegalOrBadPropertyFormatException (String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new exception with the specified cause and a detail
         * message of {@code (cause==null ? null : cause.toString())} (which
         * typically contains the class and detail message of {@code cause}).
         * This constructor is useful for exceptions that are little more than
         * wrappers for other throwables (for example, {@link
         * PrivilegedActionException}).
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A {@code null} value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.4
         */
        public IllegalOrBadPropertyFormatException (Throwable cause) {
            super(cause);
        }
    }
}
