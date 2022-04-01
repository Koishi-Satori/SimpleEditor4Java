package top.kkoishi.easy.lang;

import java.util.HashMap;

/**
 * @author KKoishi_
 */
public final class Unicode {

    private static final HashMap<Character, Byte> IGNORE_MAP = new HashMap<>(100) {{
        put('(', null);
        put(')', null);
        put('[', null);
        put(']', null);
        put('"', null);
        put('\'', null);
        put('-', null);
        put('+', null);
        put('*', null);
        put('/', null);
        put('&', null);
        put('^', null);
        put('%', null);
        put('$', null);
        put('#', null);
        put('@', null);
        put('!', null);
        put('~', null);
        put('?', null);
        put('<', null);
        put('>', null);
        put('.', null);
        put(',', null);
    }};

    private Unicode () {
    }

    public static String encode (String utfString) {
        final char[] utfChars = utfString.toCharArray();
        final StringBuilder uni = new StringBuilder();
        for (final char utfChar : utfChars) {
            uni.append(encode(utfChar));
        }
        return uni.toString();
    }

    public static String encodeExcept (String utfString) {
        final char[] utfChars = utfString.toCharArray();
        final StringBuilder uni = new StringBuilder();
        for (final char utfChar : utfChars) {
            uni.append(encodeExcept(utfChar));
        }
        return uni.toString();
    }

    public static String encode (char utfChar) {
        final String hexB = Integer.toHexString(utfChar);
        return hexB.length() <= 2 ? "\\u00" + hexB : "\\u" + hexB;
    }

    public static String encodeExcept (char utfChar) {
        if (test(utfChar)) {
            return String.valueOf(utfChar);
        }
        final String hexB = Integer.toHexString(utfChar);
        return hexB.length() <= 2 ? "\\u00" + hexB : "\\u" + hexB;
    }

    public static boolean test (char utfChar) {
        return utfChar >= 'a' && utfChar <= 'z' || (utfChar >= 'A' && utfChar <= 'Z') || IGNORE_MAP.containsKey(utfChar);
    }
}
