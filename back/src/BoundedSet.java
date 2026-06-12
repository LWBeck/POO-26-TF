import java.util.HashSet;
import java.util.Set;

public class BoundedSet<E> {
    private final Set<E> set;
    private final int capacity;

    public BoundedSet(int capacity) {
        this.capacity = capacity;
        this.set = new HashSet<>();
    }

    public boolean add(E element) {
        if (set.size() >= capacity) {
            return false; // Set is full
        }
        return set.add(element);
    }

    public boolean contains(E element) {
        return set.contains(element);
    }

    public int size() {
        return set.size();
    }
}
