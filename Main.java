package Multilevelsystem;

import java.util.*;

// Abstract Cache class
abstract class Cache {
    int capacity;
    String evictionPolicy;

    Cache(int capacity, String evictionPolicy) {
        this.capacity = capacity;
        this.evictionPolicy = evictionPolicy;
    }

    abstract String get(String key);

    abstract void put(String key, String value);

    abstract void promote(String key, String value);

    abstract void evict();

    public abstract String toString(); // Override to print cache contents
}

// LRU Cache class
class LRUCache extends Cache {
    LinkedHashMap<String, String> cache;

    LRUCache(int capacity, String evictionPolicy) {
        super(capacity, evictionPolicy);
        cache = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    @Override
    String get(String key) {
        return cache.getOrDefault(key, null);
    }

    @Override
    void put(String key, String value) {
        if (cache.size() >= capacity) {
            evict();
        }
        cache.put(key, value);
    }

    @Override
    void promote(String key, String value) {
        cache.put(key, value);
    }

    @Override
    void evict() {
        Iterator<String> it = cache.keySet().iterator();
        if (it.hasNext()) {
            cache.remove(it.next());
        }
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}

// LFU Cache class
class LFUCache extends Cache {
    HashMap<String, String> cache;
    HashMap<String, Integer> freqMap;

    LFUCache(int capacity, String evictionPolicy) {
        super(capacity, evictionPolicy);
        cache = new HashMap<>();
        freqMap = new HashMap<>();
    }

    @Override
    String get(String key) {
        if (cache.containsKey(key)) {
            freqMap.put(key, freqMap.getOrDefault(key, 0) + 1);
            return cache.get(key);
        }
        return null;
    }

    @Override
    void put(String key, String value) {
        if (cache.size() >= capacity) {
            evict();
        }
        cache.put(key, value);
        freqMap.put(key, 1);
    }

    @Override
    void promote(String key, String value) {
        cache.put(key, value);
        freqMap.put(key, freqMap.getOrDefault(key, 0) + 1);
    }

    @Override
    void evict() {
        String leastFreqKey = null;
        int minFreq = Integer.MAX_VALUE;
        for (String key : freqMap.keySet()) {
            if (freqMap.get(key) < minFreq) {
                minFreq = freqMap.get(key);
                leastFreqKey = key;
            }
        }
        if (leastFreqKey != null) {
            cache.remove(leastFreqKey);
            freqMap.remove(leastFreqKey);
        }
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}

// Multilevel Cache System class
class MultilevelCacheSystem {
    List<Cache> cacheLevels = new ArrayList<>();

    void addCacheLevel(int capacity, String evictionPolicy) {
        if (evictionPolicy.equals("LRU")) {
            cacheLevels.add(new LRUCache(capacity, evictionPolicy));
        } else if (evictionPolicy.equals("LFU")) {
            cacheLevels.add(new LFUCache(capacity, evictionPolicy));
        }
    }

    String get(String key) {
        for (int i = 0; i < cacheLevels.size(); i++) {
            Cache cache = cacheLevels.get(i);
            String value = cache.get(key);
            if (value != null) {
                if (i > 0) {
                    
                    cacheLevels.get(0).put(key, value);
                }
                return value;
            }
        }
        System.out.println("Cache Miss");
        return null;
    }

    void put(String key, String value) {
        Cache l1Cache = cacheLevels.get(0);
        l1Cache.put(key, value);
    }

    private void promote(String key, String value) {
        Cache l1Cache = cacheLevels.get(0);
        l1Cache.promote(key, value);
    }

    void printCache() {
        for (int i = 0; i < cacheLevels.size(); i++) {
            Cache cache = cacheLevels.get(i);
            System.out.println("L" + (i + 1) + " Cache: " + cache);
        }
    }
}

// Main class to test the cache system
public class Main {
    public static void main(String[] args) {
        MultilevelCacheSystem cacheSystem = new MultilevelCacheSystem();
        cacheSystem.addCacheLevel(3, "LRU");
        cacheSystem.addCacheLevel(2, "LFU");

        cacheSystem.put("A", "1");
        cacheSystem.put("B", "2");
        cacheSystem.put("C", "3");
        cacheSystem.get("A");
        cacheSystem.put("D", "4");
        cacheSystem.get("C");

        cacheSystem.printCache();
    }
}







