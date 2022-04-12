package top.kkoishi.easy.lang;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class BoyerMoore {

    private BoyerMoore () throws IllegalAccessException {
        throw new IllegalAccessException();
    }

//    public static void main (String[] args) {
//        String text = "HERE IS A SIMPLE EXAMPLE EXAMPLE EXAMPLE";
//        String pattern = "EXAMPLE";
//        BoyerMoore bm = new BoyerMoore();
//        System.out.println(bm.boyerMoore(pattern, text));
//    }

    private static void preBmBc (String pattern, int patLength, Map<String, Integer> bmBc) {
        for (int i = patLength - 2; i >= 0; i--) {
            if (!bmBc.containsKey(String.valueOf(pattern.charAt(i)))) {
                bmBc.put(String.valueOf(pattern.charAt(i)), patLength - i - 1);
            }
        }
    }

    private static void suffix (String pattern, int patLength, int[] suffix) {
        suffix[patLength - 1] = patLength;
        int q;
        for (int i = patLength - 2; i >= 0; i--) {
            q = i;
            while (q >= 0 && pattern.charAt(q) == pattern.charAt(patLength - 1 - i + q)) {
                q--;
            }
            suffix[i] = i - q;
        }
    }

    private static void preBmGs (String pattern, int patLength, int[] bmGs) {
        int i, j;
        int[] suffix = new int[patLength];
        suffix(pattern, patLength, suffix);
        for (i = 0; i < patLength; i++) {
            bmGs[i] = patLength;
        }
        j = 0;
        for (i = patLength - 1; i >= 0; i--) {
            if (suffix[i] == i + 1) {
                for (; j < patLength - 1 - i; j++) {
                    if (bmGs[j] == patLength) {
                        bmGs[j] = patLength - 1 - i;
                    }
                }
            }
        }
        for (i = 0; i < patLength - 1; i++) {
            bmGs[patLength - 1 - suffix[i]] = patLength - 1 - i;
        }
    }

    private static int getBmBc (String c, Map<String, Integer> bmBc, int m) {
        return bmBc.getOrDefault(c, m);
    }

    public static List<Integer> boyerMoore (String pattern, String text) {
        int m = pattern.length(), n = text.length();
        if (m > n) {
            return new LinkedList<>();
        }
        final Map<String, Integer> bmBc = new HashMap<>();
        final int[] bmGs = new int[m];
        final LinkedList<Integer> res = new LinkedList<>();
        //preprocessing
        preBmBc(pattern, m, bmBc);
        preBmGs(pattern, m, bmGs);
        //searching
        int j = 0, i;
        while (j <= n - m) {
            i = m - 1;
            while (i >= 0 && pattern.charAt(i) == text.charAt(i + j)) {
                i--;
            }
            if (i < 0) {
                res.add(j);
                j += bmGs[0];
            } else {
                j += Math.max(bmGs[i], getBmBc(String.valueOf(text.charAt(i + j)), bmBc, m) - m + 1 + i);
            }
        }
        return res;
    }
}
