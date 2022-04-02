package top.kkoishi.easy.lang;

import java.util.*;

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
        put(' ', null);
        put('{', null);
        put('}', null);
        put('|', null);
        put('`', null);
        put('\t', null);
        put('\n', null);
        put('\r', null);
        put('\b', null);
        put('\\', null);
        put('_', null);
        put('=', null);
        put(';', null);
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
        return utfChar >= 'a' && utfChar <= 'z' || (utfChar >= 'A' && utfChar <= 'Z') || (utfChar >= '0' && utfChar <= '9') || IGNORE_MAP.containsKey(utfChar);
    }

    public static String decode (String unicodeStr) {
        StringBuilder sb = new StringBuilder();
        StringBuilder buf;
        final String[] ss = cutUnicode(unicodeStr);
        for (final String s : ss) {
            if (s.length() != 0) {
                if (s.startsWith("\\u")) {
                    final String hex = s.substring(2, 6);
                    final String rest = s.substring(6);
                    sb.append(decode0(hex)).append(rest);
                } else {
                    sb.append(s);
                }
            }
        }
        return sb.toString();
    }

    private static String[] cutUnicode (String unicodeStr) {
        final List<String> arr = new ArrayList<>();
        final char[] charArray = unicodeStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            final StringBuilder buf = new StringBuilder();
            final char c = charArray[i];
            if (c == '\\') {
                if (i + 1 < charArray.length) {
                    if (charArray[i + 1] == 'u') {
                        buf.append("\\u");
                        ++i;
                        buf.append(charArray[++i]);
                        buf.append(charArray[++i]);
                        buf.append(charArray[++i]);
                        buf.append(charArray[++i]);
                    } else {
                        buf.append(c);
                    }
                } else {
                    buf.append(c);
                }
            } else {
                buf.append(c);
            }
            arr.add(buf.toString());
        }
        final String[] array = new String[arr.size()];
        return arr.toArray(array);
    }

    private static char decode0 (String hexString) {
        return (char) HexFormat.fromHexDigits(hexString);
    }

    public static void main (String[] args) {
        Arrays.stream(args).map(Unicode::encodeExcept).forEach(System.out::println);
        Arrays.stream(args).map(Unicode::encodeExcept).map(Unicode::decode).forEach(System.out::println);
    }
}
