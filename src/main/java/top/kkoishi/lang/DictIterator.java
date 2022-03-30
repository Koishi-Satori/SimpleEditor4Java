package top.kkoishi.lang;

import java.util.function.Consumer;

/**
 * @author KKoishi_
 */
public interface DictIterator {
    /**
     * Test if the DictIterator has more words.
     *
     * @return if it has next word.
     */
    boolean hasNext ();

    /**
     * Get the next word of the dict.
     *
     * @return word.
     */
    String nextWord ();

    default void forEach (Consumer<String> action) {
        while (hasNext()) {
            action.accept(nextWord());
        }
    }
}
