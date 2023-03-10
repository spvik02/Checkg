package cache;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * CacheFactory is used to initialize the cache based on the specified algorithm and capacity
 */
public class CacheFactory<K, V> {

    /**
     * Create a map based on a .yaml file by the path specified in the parameter.
     * @param path path to .yaml file
     * @return Map object based on .yaml file
     */
    private Map<String, Object> getMapFromYaml(String path){
        File appYaml = new File(path);
        try {
            return new Yaml().load(new FileInputStream(appYaml));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a cache based on the algorithm and capacity specified in the file.
     * If algorithm and/or capacity is not specified then the default values are taken:
     * Algorithm.LRU for algorithm and 10 for capacity.
     */
    public Cache<K, V> createCache() {
        Algorithm algorithm;
        Algorithm defaultAlgorithm = Algorithm.LRU;
        int capacity;
        int defaultCapacity = 10;
        Map<String, Object> yamlMap = getMapFromYaml("src/main/resources/application.yml");

        if(yamlMap.containsKey("cache") && yamlMap.get("cache") != null){
            Map<String, Object> cacheMap = (Map<String, Object>) yamlMap.get("cache");
            algorithm = (cacheMap.containsKey("algorithm") && cacheMap.get("algorithm") != null)
                    ? Algorithm.valueOf((String) cacheMap.get("algorithm"))
                    : defaultAlgorithm;
            capacity = (cacheMap.containsKey("capacity") && cacheMap.get("capacity") != null)
                    ? (int) cacheMap.get("capacity")
                    : defaultCapacity;
        }else {
            algorithm = defaultAlgorithm;
            capacity = defaultCapacity;
        }
        return createCache(algorithm, capacity);
    }

    /**
     * Create a cache based on algorithm and capacity parameters.
     */
    public Cache<K, V> createCache(Algorithm algorithm, int capacity) {
        if (algorithm == Algorithm.LFU) {
            return new LfuCache<>(capacity);
        }
        return new LruCache<>(capacity);
    }

    /**
     * Enum of allowed cache algorithms.
     */
    enum Algorithm {
        /**
         * LRU - Least Recently Used.
         */
        LRU,
        /**
         * LFU - Least Frequently Used.
         */
        LFU;
    }
}
