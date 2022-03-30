package top.kkoishi.easy.util;

import java.io.Serializable;
import java.util.*;

final class CharArray implements RandomAccess, Serializable, Iterable<Character> {

    static final int INITIAL_CAPABILITY = 1 << 4;

    private transient char[] cs;

    private transient int size = 0;

    private transient int modCount = 0;

    public CharArray (char[] cs) {
        this(cs.length);
        for (char c : cs) {
            offer(c);
        }
    }

    public CharArray (int initialCapability) {
        this.cs = new char[initialCapability];
    }

    public CharArray () {
        this(INITIAL_CAPABILITY);
    }

    public CharArray (Collection<Character> c) {
        this(c.size());
        for (Character character : c) {
            offer(character);
        }
    }

    public CharArray (CharArray array) {
        this(array.cs);
    }

    private void insert (int pos, char c) {
        final char[] cpy = cs;
        cs = new char[size + 1];
        System.arraycopy(cpy, 0, cs, 0, pos);
        cs[pos] = c;
        System.arraycopy(cpy, pos, cs, pos + 1, size - pos);
        ++size;
        ++modCount;
    }

    private void increase () {
        final char[] cpy = cs;
        cs = new char[size + 1];
        System.arraycopy(cpy, 0, cs, 0, size);
        ++size;
    }

    private void decrease () {
        final char[] cpy = cs;
        cs = new char[size - 1];
        System.arraycopy(cpy, 0, cs, 0, size - 1);
        --size;
    }

    private int locate (char c) {
        if (c > cs[size - 1]) {
            return size;
        }
        return binarySearch(cs, 0, cs.length - 1, c);
    }

    public static void main (String[] args) {
        final CharArray array = new CharArray(new char[] {'b', 'd', 'g', 'f', 'v', 'x'});
        System.out.println(array);
        for (int i = 'a'; i <= 'z'; i++) {
            System.out.printf("%s->%d ", (char) i, array.locate((char) i));
            array.add((char) i);
        }
        System.out.println(array);
    }

    private int binarySearch (char[] cs, int first, int last, char c) {
        final int mid = (first + last) / 2;
        final char std = cs[mid];
        if (std == c) {
            return -1;
        } else {
            if (first >= last) {
                return mid;
            } else {
                if (std > c) {
                    return binarySearch(cs, first, mid, c);
                } else {
                    return binarySearch(cs, mid + 1, last, c);
                }
            }
        }
    }

    private static boolean checkRange (char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public boolean offer (char c) {
        if (size == 0) {
            cs[size++] = c;
            return true;
        }
        if (checkRange(c)) {
            final int pos = locate(c);
            if (pos == -1) {
                return false;
            }
            if (pos == size) {
                increase();
                cs[size - 1] = c;
                ++modCount;
            } else {
                insert(pos, c);
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean add (char c) {
        return offer(c);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Character> iterator () {
        return new CharArrayIterator();
    }

    @Override
    public String toString () {
        return Arrays.toString(cs);
    }

    private class CharArrayIterator implements Iterator<Character> {

        private final int exceptedModCount = CharArray.this.modCount;

        private int pos = -1;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext () {
            return pos + 1 < size;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Character next () {
            if (exceptedModCount != CharArray.this.modCount) {
                throw new ConcurrentModificationException();
            }
            return cs[++pos];
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection
         * is modified while the iteration is in progress in any way other than by
         * calling this method, unless an overriding class has specified a
         * concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called
         * after a call to the {@link #forEachRemaining forEachRemaining} method.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this iterator
         * @throws IllegalStateException         if the {@code next} method has not
         *                                       yet been called, or the {@code remove} method has already
         *                                       been called after the last call to the {@code next}
         *                                       method
         * @implSpec The default implementation throws an instance of
         * {@link UnsupportedOperationException} and performs no other action.
         */
        @Override
        public void remove () {
            throw new UnsupportedOperationException();
        }
    }
}
