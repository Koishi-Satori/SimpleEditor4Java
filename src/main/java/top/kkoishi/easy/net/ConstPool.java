package top.kkoishi.easy.net;

/**
 * @author KKoishi_
 */
public final class ConstPool {

    public static final byte[] REPORT_HEAD = new byte[]{0X0A, 0X0E};

    public static final byte[] REPORT_END = new byte[]{0X0A, 0X0F};

    public static final byte[] START = new byte[]{0X09, 0X0E};

    public static final byte[] END = new byte[]{0X09, 0X0F};

    public static final byte[] ACCEPT = new byte[]{0X08, 0X0E};

    public static final byte[] FINISHED = new byte[]{0X08, 0X0F};

    public static final byte[] BAD_STATE = new byte[] {0X08, 0X0A};

    private ConstPool () throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
