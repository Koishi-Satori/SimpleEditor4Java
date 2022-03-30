package top.kkoishi.easy.util;

import top.kkoishi.lang.DictAccess;
import top.kkoishi.lang.WordTokenizer;
import top.kkoishi.util.EnhancedTrie;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author KKoishi_
 */
public final class PassageMatcher {

    public static final List<String> INNER_DICT = Arrays.asList("const", "for", "while", "final", "static", "public", "private", "class", "struct", "if", "new", "import", "include",
            "std", "java", "kotlin", "io", "main", "int", "char", "long", "unsigned", "bool", "boolean", "short", "float", "double", "auto", "var", "else", "sizeof",
            "nullptr", "util", "lang", "throws", "break", "goto", "continue", "void", "args", "string", "clone", "byte", "yield", "kkoishi", "out", "cout", "cin", "in",
            "print", "println", "scanf", "printf", "switch", "case", "extend", "interface", "abstract");

    private static DictAccess INIT_TRIE;

    static {
        try {
            INIT_TRIE = readDict("./data/dict");
        } catch (IOException e) {
            e.printStackTrace();
            INIT_TRIE = new EnhancedTrie(INNER_DICT);
        }
    }

    public static PassageMatcher getInstance(String in) {
        if (in.length() == 0) {
            return new PassageMatcher(new EnhancedTrie());
        }
        final WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.load(in);
        tokenizer.tokenize();
        final DictAccess trie = new EnhancedTrie();
        trie.addAll(tokenizer.getResult());
        tokenizer.clear();
        return new PassageMatcher(trie);
    }

    private PassageMatcher (DictAccess dict) {
        this.dict.addAll(INIT_TRIE);
        this.dict.addAll(dict);
        System.out.println("Finish load Trie from Dict.");
    }

    private final DictAccess dict = new EnhancedTrie();

    private final Deque<String> added = new LinkedList<>();

    public boolean contains (String word) {
        return dict.contains(word);
    }

    public void flush (String in) {
        if (in.length() == 0) {
            return;
        }
        final WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.load(in);
        tokenizer.tokenize();
        dict.clear();
        this.dict.addAll(INIT_TRIE);
        dict.addAll(tokenizer.getResult());
        tokenizer.clear();
    }

    public void remove (String word) {
        if (added.remove(word)) {
            dict.remove(word);
        }
    }

    public List<String> predict (String prefix) {
        return dict.predict(prefix);
    }

    public void offer (LinkedList<Character> buffer) {
        final StringBuilder sb = new StringBuilder();
        buffer.forEach(sb::append);
        if (dict.add(sb.toString())) {
            System.out.println("Add new word:" + sb);
            added.offer(sb.toString());
        } else {
            System.out.println("Failed to add:" + sb);
        }
    }

    public void clear () {
        dict.clear();
        added.clear();
    }

    public static DictAccess readDict (String src) throws IOException {
        final Properties proc = new Properties();
        proc.load(new FileInputStream(src));
        final int size = Integer.parseInt(proc.getProperty("size"));
        final ArrayList<String> dict = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final String word = proc.getProperty("dict_" + i);
            System.out.println("Reading word:" + word);
            dict.add(word);
        }
        return new EnhancedTrie(dict);
    }

    public void genDict (List<String> words) throws IOException {
        final Properties properties = new Properties();
        properties.put("size", String.valueOf(words.size()));
        int i = 0;
        for (String word : words) {
            System.out.println("Writing word to dict:" + word);
            properties.put("dict_" + i, word);
            ++i;
        }
        properties.store(new FileOutputStream("./data/dict"), "initialDict");
    }
}
