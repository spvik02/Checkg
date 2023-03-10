package cache;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * LFU cache discards the least frequently used item when the cache is full to add a new item which is not in the cash.
 */
public class LfuCache<K, V> implements Cache<K, V>{
    /**
     * Map for cache with key and value
     */
    private HashMap<K, V> cache = new HashMap<>();
    /**
     * Key usage map. Key represents key. Value represents count of usage this key.
     */
    private HashMap<K, Integer> keyCounts = new HashMap<>();
    /**
     * Frequency map. Key represents count of usage. Value represents set of keys.
     */
    private HashMap<Integer, LinkedHashSet<K>> freqMap = new HashMap<>();
    private int capacity;
    private int min = -1;

    /**
     * Initializes the object with the capacity of the data structure.
     * @param capacity
     */
    LfuCache(int capacity){
        this.capacity = capacity;
        freqMap.put(1, new LinkedHashSet<>());
    }

    /**
     * Gets the value of the key if the key exists in the cache. Otherwise, returns null.
     * When the method is called, the frequency of usage increases.
     * @return value of the key if the key exists in the cache. Otherwise, returns null.
     */
    public V get(K key){
        if(!cache.containsKey(key)){
            return null;
        }
        int count = keyCounts.get(key);
        keyCounts.put(key, count+1);
        freqMap.get(count).remove(key);
        if (count == min && freqMap.get(count).size() == 0){
            min++;
        }
        if(!freqMap.containsKey(count + 1)) {
            freqMap.put(count+1, new LinkedHashSet<>());
        }
        freqMap.get(count+1).add(key);
        return cache.get(key);
    }

    /**
     * Delete key from all maps. If key was the last in the count list in frequency map, then find new min.
     */
    @Override
    public void delete(K key) {
        if(cache.containsKey(key)){
            int count = keyCounts.get(key);
            freqMap.get(count).remove(key);
            keyCounts.remove(key);
            if (count == min && freqMap.get(count).size() == 0){
                min = keyCounts.values().stream().mapToInt(Integer::intValue).min().orElse(-1);
            }
            cache.remove(key);
        }
    }

    /**
     * Evict the first least frequently used item from the cache when the cache is full. In case of the same frequency,
     * evict least recently used item from the cache when the cache is full.
     */
    public void put(K key, V value){
        if(capacity <= 0) return;
        if(cache.containsKey(key)){
            cache.put(key, value);
            get(key);
            return;
        }
        if(cache.size() >= capacity){
            K evict = freqMap.get(min).iterator().next();
            freqMap.get(min).remove(evict);
            cache.remove(evict);
            keyCounts.remove(evict);
        }
        cache.put(key, value);
        keyCounts.put(key, 1);
        min = 1;
        freqMap.get(1).add(key);
    }
}
