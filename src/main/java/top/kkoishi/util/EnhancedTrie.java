package top.kkoishi.util;

import top.kkoishi.io.Files;
import top.kkoishi.lang.DictAccess;
import top.kkoishi.lang.DictIterator;
import top.kkoishi.lang.WordTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is a trie implementation with capability to add upper-case letter(in English).
 *
 * @author KKoishi_
 */
public final class EnhancedTrie implements DictAccess {

    private static final int LETTER_AMOUNT = 26;

    private static final int EXTEND_LENGTH = 2 * LETTER_AMOUNT;

    public static final int UPPER_FIRST = 65;

    private static final int UPPER_INDEX = LETTER_AMOUNT - UPPER_FIRST;

    private static final int LOWER_FIRST = 97;

    private static final int BUFFER_SIZE = 1 << 11;

    private static final int CHAR_BUFFER_SIZE = 1 << 8;

    Node root = new Node('\u0001', false, null);

    int size = 0;

    public EnhancedTrie (DictAccess dict) {
        this();
        addAll(dict);
    }

    public EnhancedTrie () {
    }

    public EnhancedTrie (Collection<String> words) {
        addAll(words);
    }

    public static EnhancedTrie build (InputStream in) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int len;
        final byte[] buffer = new byte[BUFFER_SIZE];
        while ((len = in.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len));
        }
        return build(sb.toString());
    }

    public static EnhancedTrie build (Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int len;
        final char[] buffer = new char[CHAR_BUFFER_SIZE];
        while ((len = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, len);
        }
        return build(sb.toString());
    }

    public static EnhancedTrie build (WordTokenizer tokenizer) {
        tokenizer.tokenize();
        final EnhancedTrie trie = new EnhancedTrie();
        trie.addAll(tokenizer.getResult());
        return trie;
    }

    public static EnhancedTrie build (File file) throws IOException {
        return build(Files.read(file));
    }

    public static EnhancedTrie build (String document) {
        final WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.load(document);
        final EnhancedTrie build = build(tokenizer);
        tokenizer.clear();
        return build;
    }

    public static int char2index (char c) {
        if (c >= 'a' && c <= 'z') {
            return c - LOWER_FIRST;
        } else {
            return c + UPPER_INDEX;
        }
    }

    public static char index2lower (int index) {
        return (char) (index + LOWER_FIRST);
    }

    public static char index2upper (int index) {
        return (char) (index - UPPER_INDEX);
    }

    public static void checkRange (char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return;
        }
        throw new IllegalArgumentException();
    }

    private static Node[] basicArray () {
        Node[] nodes = new Node[LETTER_AMOUNT];
        for (int i = 0; i < LETTER_AMOUNT; i++) {
            nodes[i] = new Node(index2lower(i), false, null);
        }
        return nodes;
    }

    public static Node[] extendCharArray (Node[] arr) {
        if (arr.length > LETTER_AMOUNT) {
            return arr;
        }
        final Node[] cpy = arr;
        arr = new Node[EXTEND_LENGTH];
        System.arraycopy(cpy, 0, arr, 0, LETTER_AMOUNT);
        for (int i = LETTER_AMOUNT; i < EXTEND_LENGTH; i++) {
            arr[i] = new Node(index2upper(i), false, null);
        }
        return arr;
    }

    private void link (Node node, char[] cs, int pos) {
        if (pos >= cs.length) {
            if (node.frequency == 0) {
                node.isLeaf = true;
                ++size;
            }
            ++node.frequency;
            return;
        }
        checkRange(cs[pos]);
        if (node.frequency == 0 || node.nodes == null) {
            node.nodes = basicArray();
        }
        if (node.frequency == 0 || !node.isLeaf) {
            ++node.frequency;
        }
        if (Trie.extend(cs[pos])) {
            node.nodes = extendCharArray(node.nodes);
        }
        link(node.nodes[char2index(cs[pos])], cs, pos + 1);
    }

    private boolean unlink (Node node, char[] cs, int pos) {
        if (pos >= cs.length) {
            if (node.frequency == 1) {
                node.isLeaf = false;
                node.nodes = null;
                --size;
            }
            --node.frequency;
            return true;
        } else if (node.frequency > 0) {
            if (range(cs[pos])) {
                return unlink(node.nodes[char2index(cs[pos])], cs, pos + 1);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void dfs (Node node, StringBuilder sb, Map<String, Integer> dict) {
        if (node.frequency > 0) {
            sb.append(node.c);
        }
        if (node.isLeaf || node.nodes == null) {
            if (node.frequency == 0) {
                return;
            }
            dict.put(sb.toString(), node.frequency);
            int len = sb.toString().length() - 1;
            if (len < 0) {
                return;
            }
            if (node.nodes != null) {
                for (Node child : node.nodes) {
                    if (child.frequency != 0) {
                        dfs(child, new StringBuilder(sb), dict);
                    }
                }
            }
            sb.deleteCharAt(len);
        } else {
            for (Node child : node.nodes) {
                if (child.frequency != 0) {
                    dfs(child, new StringBuilder(sb), dict);
                }
            }
        }
    }

    private void dfs (Node node, StringBuilder sb, List<String> res) {
        if (node.frequency > 0) {
            sb.append(node.c);
        }
        if (node.isLeaf || node.nodes == null) {
            if (node.frequency == 0) {
                return;
            }
            res.add(sb.toString());
            int len = sb.toString().length() - 1;
            if (len < 0) {
                return;
            }
            if (node.nodes != null) {
                for (Node child : node.nodes) {
                    if (child.frequency == 0) {
                        continue;
                    }
                    dfs(child, new StringBuilder(sb), res);
                }
            }
            sb.deleteCharAt(len);
        } else {
            for (Node child : node.nodes) {
                if (child.frequency == 0) {
                    continue;
                }
                dfs(child, new StringBuilder(sb), res);
            }
        }
    }

    private List<String> predict (final char[] cs) {
        List<String> res = new LinkedList<>();
        int pos = 0;
        final int len = cs.length;
        Node pointer = root;
        while (pointer != null && pointer.nodes != null && pos < len) {
            if (!range(cs[pos])) {
                return res;
            }
            final int loc = char2index(cs[pos]);
            try {
                pointer = pointer.nodes[loc];
            } catch (ArrayIndexOutOfBoundsException e) {
                return res;
            }
            ++pos;
        }
        if (pointer == null) {
            return res;
        }
        if (pointer.nodes == null) {
            if (pos <= len) {
                return res;
            } else {
                final StringBuilder sb = new StringBuilder();
                for (char c : cs) {
                    sb.append(c);
                }
                res.add(sb.append(pointer.c).toString());
            }
        } else {
            final StringBuilder sb = new StringBuilder();
            for (char c : cs) {
                sb.append(c);
            }
            //dfs the node.
            for (Node child : pointer.nodes) {
                final List<String> temp = new LinkedList<>();
                if (child != null) {
                    dfs(child, new StringBuilder(), temp);
                }
                for (String s : temp) {
                    res.add(sb + s);
                }
            }
        }
        return res;
    }

    private boolean contains (Node node, char[] cs, int pos) {
        if (node.frequency == 0) {
            return false;
        }
        if (pos >= cs.length) {
            return node.isLeaf;
        }
        if (node.nodes != null) {
            if (range(cs[pos])) {
                return contains(node.nodes[char2index(cs[pos])], cs, pos + 1);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean range (char c) {
        return (c >= 'a' && c <= 'z') || (c <= 'Z' && c >= 'A');
    }

    /**
     * The node of the trie.
     *
     * @author KKoishi_
     */
    static class Node implements Serializable, Comparable<Node> {

        char c;

        boolean isLeaf;

        int frequency = 0;

        Node[] nodes;

        public Node (char c, boolean isLeaf, Node[] nodes) {
            this.c = c;
            this.isLeaf = isLeaf;
            this.nodes = nodes;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * <p>The implementor must ensure {@link Integer#signum
         * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
         * all {@code x} and {@code y}.  (This implies that {@code
         * x.compareTo(y)} must throw an exception if and only if {@code
         * y.compareTo(x)} throws an exception.)
         *
         * <p>The implementor must also ensure that the relation is transitive:
         * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
         * {@code x.compareTo(z) > 0}.
         *
         * <p>Finally, the implementor must ensure that {@code
         * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
         * == signum(y.compareTo(z))}, for all {@code z}.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         * @apiNote It is strongly recommended, but <i>not</i> strictly required that
         * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
         * class that implements the {@code Comparable} interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         */
        @Override
        public int compareTo (Node o) {
            return this.c - o.c;
        }
    }

    /**
     * Add a new word into the dict.
     *
     * @param word word to be added.
     * @return true if succ
     */
    @Override
    public boolean add (String word) {
        link(root, word.toCharArray(), 0);
        return true;
    }

    public static void main (String[] args) throws IOException {
        final DictAccess dict = build(new File("./test.cpp"));
        System.out.println(dict.toMap());
        System.out.println(dict.predict("T"));
        System.out.println(dict.predict("t"));
        System.out.println(dict.contains("dfsImpl"));
        System.out.println(dict.remove("dfsImpl"));
        System.out.println(dict.toMap().containsKey("dfsImpl"));
    }

    /**
     * Get the word amount.
     *
     * @return amount.
     */
    @Override
    public int size () {
        return size;
    }

    /**
     * Remove a word.
     *
     * @param word the word to be removed.
     * @return true if succ.
     */
    @Override
    public boolean remove (String word) {
        return unlink(root, word.toCharArray(), 0);
    }

    /**
     * If contains the word,return true.
     *
     * @param word word to be added.
     * @return true if contains the word.
     */
    @Override
    public boolean contains (String word) {
        return contains(root, word.toCharArray(), 0);
    }

    /**
     * Get a map instance which its entry contains the word and frequency.
     *
     * @return map
     */
    @Override
    public Map<String, Integer> toMap () {
        if (size == 0) {
            return new HashMap<>(1);
        }
        Map<String, Integer> map = new HashMap<>(3 * size);
        for (Node child : root.nodes) {
            dfs(child, new StringBuilder(), map);
        }
        return map;
    }

    /**
     * Get the word collection.
     *
     * @return a list.
     */
    @Override
    public List<String> getWords () {
        if (size == 0) {
            return new LinkedList<>();
        } else {
            List<String> words = new ArrayList<>(size);
            for (Node node : root.nodes) {
                dfs(node, new StringBuilder(), words);
            }
            return words;
        }
    }

    /**
     * Predict word.
     *
     * @param prefix the first part of the word.
     * @return words.
     */
    @Override
    public List<String> predict (String prefix) {
        return predict(prefix.toCharArray());
    }

    /**
     * Clear all the elements.
     */
    @Override
    public void clear () {
        //TODO
    }

    @Override
    public DictIterator iterator () {
        return new Iterator();
    }

    private class Iterator implements DictIterator {

        private final List<String> words = getWords();

        private int pos = -1;

        /**
         * Test if the DictIterator has more words.
         *
         * @return if it has next word.
         */
        @Override
        public boolean hasNext () {
            return pos + 1 < size;
        }

        /**
         * Get the next word of the dict.
         *
         * @return word.
         */
        @Override
        public String nextWord () {
            return words.get(++pos);
        }
    }
}
