package cache;

import model.Product;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class LfuCache<K, V> implements Cache<K, V>{
    private HashMap<K, V> cache = new HashMap<>();
    private HashMap<K, Integer> keyCounts = new HashMap<>();
    private HashMap<Integer, LinkedHashSet<K>> freqMap = new HashMap<>();
    private int capacity;
    private int min = -1;

    LfuCache(int capacity){
        this.capacity = capacity;
        freqMap.put(1, new LinkedHashSet<>());
    }

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
