package dev.kayange.sdms.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheStore<K, V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit time) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, time )
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    public V get(@NotNull K key) {
        log.info("Retrieving key {} from the cache", key.toString());
        return cache.getIfPresent(key);
    }

    public void put(@NotNull K key, @NotNull V value) {
        log.info("Setting value for key {}", key.toString());
        cache.put(key, value);
    }

    public void evict(@NotNull K key) {
        log.info("Removing key {} from the cache", key.toString());
        cache.invalidate(key);
    }
}
