package com.agile.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class MultiLevelCacheManager extends AbstractCacheManager {

    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineNativeCache;
    private final CacheManager redisCacheManager;   // 类型改为 CacheManager
    private final MultiLevelCacheProperties properties;
    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    // 构造函数参数类型改为 CacheManager
    public MultiLevelCacheManager(com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineNativeCache,
                                  CacheManager redisCacheManager,
                                  MultiLevelCacheProperties properties) {
        this.caffeineNativeCache = caffeineNativeCache;
        this.redisCacheManager = redisCacheManager;
        this.properties = properties;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return Collections.emptyList();
    }

    @Override
    protected Cache getMissingCache(String name) {
        return cacheMap.computeIfAbsent(name, cacheName -> {
            Cache redisCache = null;
            if (redisCacheManager != null) {
                redisCache = redisCacheManager.getCache(cacheName); // 接口方法，安全
            }
            boolean allowNullValues = properties.getRedis().isCacheNullValues();
            return new MultiLevelCache(cacheName, caffeineNativeCache, redisCache, allowNullValues);
        });
    }
}