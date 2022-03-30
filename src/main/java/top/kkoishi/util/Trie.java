package top.kkoishi.util;

import top.kkoishi.lang.DictAccess;
import top.kkoishi.lang.DictIterator;
import top.kkoishi.lang.WordTokenizer;
import top.kkoishi.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * First edition of the Trie.
 *
 * @author KKoishi_
 */
public final class Trie implements DictAccess {

    @Override
    public int size () {
        return size;
    }

    public static Trie build (File f) throws IOException {
        final String str = Files.read(f);
        final WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.load(str);
        tokenizer.tokenize();
        Trie trie = new Trie(tokenizer.getResult());
        tokenizer.clear();
        return trie;
    }

    public boolean contains (char[] cs, int pos, TrieNode node) {
        if (node == null) {
            return false;
        }
        if (pos >= cs.length) {
            return node.isLeaf;
        }
        if (node.children != null) {
            final int index = charToIndex(cs[pos]);
            if (index < 0 || index > 26) {
                return false;
            }
            return contains(cs, pos + 1, node.children[index]);
        } else {
            return false;
        }
    }

    private static class TrieNode implements Serializable {
        final char c;
        int frequency = 0;
        TrieNode[] children = null;
        boolean isLeaf = false;

        public TrieNode (char c) {
            this.c = c;
        }

        @Override
        public String toString () {
            if (frequency == 0) {
                return "";
            }
            final String arr = array2string(children);
            return "TrieNode{" +
                    "char=" + c +
                    ", frequency=" + frequency +
                    ("".equals(arr) ? "" : (",\nchildren=" + arr)) +
                    "}";
        }

        public Character getC () {
            return c;
        }
    }

    public static int charToIndex (char c) {
        return (int) c - 97;
    }

    public static char indexToChar (int index) {
        return (char) (97 + index);
    }

    private TrieNode root;
    private int size = 0;

    private void unlink (TrieNode n, char[] cs, int pos) {
        if (pos >= cs.length) {
            if (n.frequency == 1) {
                n.isLeaf = false;
                n.children = null;
                --size;
            }
            --n.frequency;
            return;
        }
        if (n.frequency > 0) {
            for (TrieNode child : n.children) {
                if (child.c == cs[pos]) {
                    unlink(child, cs, pos + 1);
                    return;
                }
            }
        }
        throw new NoSuchElementException();
    }

    public static boolean extend (char c) {
        return c >= 'A' && c <= 'Z';
    }

    private void link (TrieNode node, char[] cs, int pos) {
        if (pos >= cs.length) {
            if (node.frequency == 0) {
                node.isLeaf = true;
                ++size;
            }
            ++node.frequency;
            return;
        }
        if (node.frequency == 0 || node.children == null) {
            node.children = nodes();
        }
        if (node.frequency == 0 || !node.isLeaf) {
            ++node.frequency;
        }
        link(node.children[charToIndex(cs[pos])], cs, pos + 1);
    }

    private List<String> levelOrderTraversal () {
        List<String> res = new ArrayList<>(size);
        final Queue<TrieNode> queue = new LinkedList<>();
        final StringBuilder sb = new StringBuilder();
        for (TrieNode child : root.children) {
            if (child.frequency > 0) {
                queue.offer(child);
            }
        }
        while (!queue.isEmpty()) {
            System.out.println(sb);
            final int size = queue.size();
            for (int i = 0; i < size; i++) {
                final TrieNode node = queue.poll();
                assert node != null : "Null TrieNode";
                sb.append(node.c);
                if (node.children == null) {
                    sb.append("|");
                    continue;
                }
                final String str = sb.toString();
                for (TrieNode child : node.children) {
                    if (child.frequency > 0) {
                        sb.append(str);
                        queue.offer(child);
                    }
                }
            }
        }
        for (String s : sb.toString().split("\\|")) {
            res.add(s);
        }
        return res;
    }

    private void dfs (TrieNode node, StringBuilder sb, Map<String, Integer> dict) {
        if (node.frequency > 0) {
            sb.append(node.c);
        }
        if (node.isLeaf || node.children == null) {
            if (node.frequency == 0) {
                return;
            }
            dict.put(sb.toString(), node.frequency);
            int len = sb.toString().length() - 1;
            if (len < 0) {
                return;
            }
            if (node.children != null) {
                for (TrieNode child : node.children) {
                    if (child.frequency == 0) {
                        continue;
                    }
                    dfs(child, new StringBuilder(sb), dict);
                }
            }
            sb.deleteCharAt(len);
        } else {
            for (TrieNode child : node.children) {
                if (child.frequency == 0) {
                    continue;
                }
                dfs(child, new StringBuilder(sb), dict);
            }
        }
    }

    private void dfs (TrieNode node, StringBuilder sb, List<String> res) {
        if (node.frequency > 0) {
            sb.append(node.c);
        }
        if (node.isLeaf || node.children == null) {
            if (node.frequency == 0) {
                return;
            }
            res.add(sb.toString());
            int len = sb.toString().length() - 1;
            if (len < 0) {
                return;
            }
            if (node.children != null) {
                for (TrieNode child : node.children) {
                    if (child.frequency == 0) {
                        continue;
                    }
                    dfs(child, new StringBuilder(sb), res);
                }
            }
            sb.deleteCharAt(len);
            //}
        } else {
            for (TrieNode child : node.children) {
                if (child.frequency == 0) {
                    continue;
                }
                dfs(child, new StringBuilder(sb), res);
            }
        }
    }

    private static char[] letters () {
        char[] cs = new char[26];
        for (int i = 0; i < 26; i++) {
            cs[i] = (char) (i + 97);
        }
        return cs;
    }

    private static TrieNode[] nodes () {
        TrieNode[] nodes = new TrieNode[26];
        for (int i = 0; i < 26; i++) {
            nodes[i] = new TrieNode(indexToChar(i));
        }
        return nodes;
    }

    public Trie () {
        root = new TrieNode('\u0001');
    }

    public Trie (Trie trie) {
        this(trie.toMap());
    }

    public Trie (Map<String, Integer> dict) {
        this();
        addAll(dict);
    }

    public Trie (Collection<String> c) {
        this();
        addAll(c);
    }

    public Trie (String[] strings) {
        this();
        addAll(strings);
    }

    @Override
    public boolean add (String str) {
        for (final char c : str.toLowerCase(Locale.ROOT).toCharArray()) {
            if (!(c >= 'a' && c <= 'z')) {
                return false;
            }
        }
        link(root, str.toLowerCase(Locale.ROOT).toCharArray(), 0);
        return true;
    }

    @Override
    public boolean remove (String word) {
        final char[] cs = word.toCharArray();
        for (TrieNode child : root.children) {
            if (cs[0] == child.c) {
                unlink(child, cs, 1);
                return false;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public DictIterator iterator () {
        return null;
    }

    public boolean addAll (Map<String, Integer> dict) {
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            final int f = entry.getValue();
            for (int i = 0; i < f; i++) {
                add(entry.getKey());
            }
        }
        return true;
    }

    public boolean addAll (Trie trie) {
        return addAll(trie.toMap());
    }

    @Override
    public boolean addAll (Collection<String> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll (String[] es) {
        for (String e : es) {
            add(e);
        }
        return true;
    }

    @Override
    public boolean contains (String word) {
        return contains(word.toCharArray(), 0, root);
    }

    @Override
    public List<String> predict (String prefix) {
        return predict(prefix.toCharArray());
    }

    private List<String> predict (final char[] cs) {
        List<String> res = new LinkedList<>();
        int pos = 0;
        final int len = cs.length;
        TrieNode pointer = root;
        while (pointer != null && pointer.children != null && pos < len) {
            final int loc = charToIndex(cs[pos]);
            if (loc < 0 || loc > 25) {
                return res;
            }
            pointer = pointer.children[loc];
            ++pos;
        }
        if (pointer == null) {
            return res;
        }
        if (pointer.children == null) {
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
            //bfs the node.
            for (TrieNode child : pointer.children) {
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

    @Override
    public String toString () {
        return "Trie{" +
                "root=" + root +
                '}';
    }

    @Override
    public Map<String, Integer> toMap () {
        if (size == 0) {
            return new HashMap<>(1);
        }
        Map<String, Integer> map = new HashMap<>(3 * size);
        for (TrieNode child : root.children) {
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
            return new ArrayList<>(1);
        }
        List<String> res = new ArrayList<>(size);
        for (TrieNode child : root.children) {
            dfs(child, new StringBuilder(), res);
        }
        return res;
    }

    @Override
    public void clear () {
        clear(root);
    }

    private void clear (TrieNode root) {
        if (root.children != null) {
            for (TrieNode node : root.children) {
                clear(node);
            }
            root.children = null;
        }
        root.frequency = 0;
        root = null;
    }

    private static String array2string (TrieNode[] es) {
        if (es == null) {
            return "";
        }
        final int iMax = es.length - 1;
        if (iMax == -1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; ; ++i) {
            if (i == iMax) {
                return sb.append(']').toString();
            }
            if (es[i] == null || es[i].frequency == 0) {
                continue;
            }
            sb.append(es[i].toString());
            sb.append(", ");
        }
    }
}
