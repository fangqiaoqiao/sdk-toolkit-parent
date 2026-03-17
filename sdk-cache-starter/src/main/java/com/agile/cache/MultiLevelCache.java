package com.agile.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

public class MultiLevelCache extends AbstractValueAdaptingCache {

    private final String name;
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache;
    private final Cache redisCache;
    private final boolean allowNullValues;

    public MultiLevelCache(String name,
                           com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache,
                           Cache redisCache,
                           boolean allowNullValues) {
        super(allowNullValues);
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
        this.allowNullValues = allowNullValues;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        // 1. 先从 Caffeine 查询
        Object value = caffeineCache.getIfPresent(key);
        if (value != null) {
            return value;
        }
        // 2. 再从 Redis 查询（如果存在）
        if (redisCache != null) {
            Cache.ValueWrapper wrapper = redisCache.get(key);
            if (wrapper != null) {
                Object redisValue = wrapper.get();
                // 写入 Caffeine 作为本地缓存
                caffeineCache.put(key, redisValue);
                return redisValue;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        // 1. 从底层存储获取原始值，并转换为用户可见的值（例如将 NullValue 转为 null）
        Object value = fromStoreValue(lookup(key));
        // 2. 类型检查（type 通常不为 null，此处防御性判断）
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        // 3. 安全转换：如果 type 存在则使用 cast，否则直接强转（理论上不会走到此处）
        // 此处转换是安全的，因为已通过类型检查
        @SuppressWarnings("unchecked")
        T result = type != null ? type.cast(value) : (T) value;
        return result;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        // 1. 尝试从缓存获取并转换
        Object cached = fromStoreValue(lookup(key));
        if (cached != null) {
            // 此处无法进行类型安全的转换，但返回值一定与 Callable 的返回类型兼容，因此抑制警告
            @SuppressWarnings("unchecked")
            T result = (T) cached;
            return result;
        }
        // 2. 若缓存未命中，则执行 valueLoader 加载新值
        try {
            T loaded = valueLoader.call();
            // 存入缓存（put 内部会调用 toStoreValue 处理 null 值）
            put(key, loaded);
            return loaded;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        Object storeValue = toStoreValue(value);
        caffeineCache.put(key, storeValue);
        if (redisCache != null) {
            redisCache.put(key, storeValue);
        }
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object storeValue = toStoreValue(value);
        // 优先使用 Redis 的原子 putIfAbsent 操作
        if (redisCache != null) {
            ValueWrapper wrapper = redisCache.putIfAbsent(key, storeValue);
            if (wrapper != null) {
                // Redis 中已存在，将其值同步到 Caffeine
                Object existingValue = wrapper.get();
                caffeineCache.put(key, existingValue);
                return wrapper;
            }
        }
        // Redis 不存在或 Redis 未启用，直接写入两级缓存
        caffeineCache.put(key, storeValue);
        return null;
    }

    @Override
    public void evict(Object key) {
        caffeineCache.invalidate(key);
        if (redisCache != null) {
            redisCache.evict(key);
        }
    }

    @Override
    public void clear() {
        caffeineCache.invalidateAll();
        if (redisCache != null) {
            redisCache.clear();
        }
    }
}