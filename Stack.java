import java.util.*;

/**
* Test case of editor.
*
* @author KKoishi_
*/
public final class Stack<T> implement Iterable<T> {
    static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString () {
            return "Node{data='" + data + "', next='" + next + "};
        }
    }

    private transient int size = 0;
    
    private transient Node<T> root = null;

    private transient int modCount = 0;

    
}
