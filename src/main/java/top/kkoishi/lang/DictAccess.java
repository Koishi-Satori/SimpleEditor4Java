package top.kkoishi.lang;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author KKoshi_
 */
public interface DictAccess {
    /**
     * Add a new word into the dict.
     *
     * @param word word to be added.
     * @return
     */
    boolean add (String word);

    /**
     * Add all the words.
     *
     * @param words words.
     * @return true if succ.
     */
    default boolean addAll (Collection<String> words) {
        for (String word : words) {
            add(word);
        }
        return true;
    }

    /**
     * Add all the words in the dict.
     *
     * @param dict dict
     */
    default void addAll (DictAccess dict) {
        dict.getWords().forEach(this::add);
    }

    /**
     * Add all the words.
     *
     * @param words words.
     * @return true if succ.
     */
    default boolean addAll (String... words) {
        for (String word : words) {
            add(word);
        }
        return true;
    }

    /**
     * Get the word amount.
     *
     * @return amount.
     */
    int size ();

    /**
     * Remove a word.
     *
     * @param word the word to be removed.
     * @return true if succ.
     */
    boolean remove (String word);

    /**
     * Remove if the word matches the condition.
     *
     * @param predicate condition.
     */
    default void removeIf (Predicate<String> predicate) {
        final List<String> words = getWords();
        for (String word : words) {
            if (predicate.test(word)) {
                remove(word);
            }
        }
    }

    /**
     * Remove all words.
     *
     * @param words dict.
     */
    default void removeAll (Collection<String> words) {
        words.forEach(this::remove);
    }

    /**
     * Remove all words.
     *
     * @param dict dict.
     */
    default void removeAll (DictAccess dict) {
        dict.forEach(this::remove);
    }

    default void forEach (Consumer<String> action) {
        this.getWords().forEach(action);
    }

    DictIterator iterator ();


    /**
     * If contains the word,return true.
     *
     * @return true if contains the word.
     * @param word word.
     */
    boolean contains (String word);

    /**
     * Get a map instance which its entry contains the word and frequency.
     *
     * @return map
     */
    Map<String, Integer> toMap ();

    /**
     * Get the word collection.
     *
     * @return a list.
     */
    List<String> getWords ();

    /**
     * Predict word.
     *
     * @param prefix the first part of the word.
     * @return words.
     */
    List<String> predict (String prefix);

    /**
     * Clear all the elements.
     */
    void clear ();
}
