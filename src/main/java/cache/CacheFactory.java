package cache;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class CacheFactory<K, V> {

    private Map<String, Object> getMapFromYaml(String path){
        File appYaml = new File(path);
        try {
            return new Yaml().load(new FileInputStream(appYaml));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
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

    public Cache<K, V> createCache(Algorithm algorithm, int capacity) {
        if (algorithm == Algorithm.LFU) {
            return new LfuCache<>(capacity);
        }
        return new LruCache<>(capacity);
    }

    enum Algorithm {
        LRU,
        LFU;
    }
}
