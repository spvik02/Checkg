package cache;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class LruCache<K, V> implements Cache<K, V> {
    private int capacity;
    private LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public V get(K key) {
        return this.map.get(key);
    }

    public void put(K key, V value) {
        if (!this.map.containsKey(key) && this.map.size() == this.capacity) {
            Iterator<K> it = this.map.keySet().iterator();
            it.next();
            it.remove();
        }
        this.map.put(key, value);
    }
}
