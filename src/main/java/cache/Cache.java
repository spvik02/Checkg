package cache;

/**
 * Cache interface
 * A cache stores data so that future requests for that data can be served faster.
 * The data stored in a cache might be the result of an earlier computation or a copy of data stored elsewhere.
 * @param <K> the type of keys maintained by cache
 * @param <V> the type of values maintained by cache
 */
public interface Cache<K, V> {
    void put(K key, V value);
    V get(K key);
    void delete(K key);
}
