package cache;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * LRU cache discards the least recently used item when the cache is full to add a new item which is not in the cash.
 */
public class LruCache<K, V> implements Cache<K, V> {
    private int capacity;
    /**
     * Map for cache with key and value
     */
    private LinkedHashMap<K, V> map;

    /**
     * Initializes the object with the capacity of the data structure.
     * @param capacity - capacity of cache
     */
    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    /**
     * Gets the value of the key if the key exists in the cache. Otherwise, returns null.
     * When the method is called, the recently usage updates
     * @return value of the key if the key exists in the cache. Otherwise, returns null.
     */
    public V get(K key) {
        return this.map.get(key);
    }

    /**
     * Delete key from all map
     */
    @Override
    public void delete(K key) {
        if(this.map.containsKey(key)){
            map.remove(key);
        }
    }

    /**
     * Evict least recently used item from the cache when the cache is full.
     */
    public void put(K key, V value) {
        if (!this.map.containsKey(key) && this.map.size() == this.capacity) {
            Iterator<K> it = this.map.keySet().iterator();
            it.next();
            it.remove();
        }
        this.map.put(key, value);
    }
}
