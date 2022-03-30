package top.kkoishi.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * @author KKoishi_
 */
public final class WordTokenizer {
    private char[] cs = null;

    private List<String> result = new LinkedList<>();

    public void load (String str) {
        cs = str.toCharArray();
    }

    public void tokenize () {
        StringBuilder sb = new StringBuilder();
        for (final char c : cs) {
            if (c == ' ' && !sb.isEmpty()) {
                result.add(sb.toString());
                sb = new StringBuilder();
            } else {
                if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
                    sb.append(c);
                } else {
                    if (sb.isEmpty()) {
                        continue;
                    }
                    result.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
    }

    public List<String> getResult () {
        return result;
    }

    public void clear () {
        cs = null;
        result.clear();
        result = null;
    }
}
