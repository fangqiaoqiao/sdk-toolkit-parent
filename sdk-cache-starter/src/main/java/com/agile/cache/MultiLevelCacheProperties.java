package com.agile.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "sdk.cache")
public class MultiLevelCacheProperties {

    /**
     * 是否启用SDK缓存，默认启用
     */
    private boolean enabled = true;

    /**
     * Caffeine本地缓存配置
     */
    private CaffeineConfig caffeine = new CaffeineConfig();

    /**
     * Redis远程缓存配置
     */
    private RedisConfig redis = new RedisConfig();

    // getters / setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public CaffeineConfig getCaffeine() { return caffeine; }
    public void setCaffeine(CaffeineConfig caffeine) { this.caffeine = caffeine; }
    public RedisConfig getRedis() { return redis; }
    public void setRedis(RedisConfig redis) { this.redis = redis; }

    public static class CaffeineConfig {
        /**
         * 初始容量
         */
        private int initialCapacity = 100;

        /**
         * 最大条目数
         */
        private long maximumSize = 10000L;

        /**
         * 最后一次访问后过期时间
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration expireAfterAccess = Duration.ofMinutes(10);

        /**
         * 是否开启软引用淘汰（默认false，使用基于大小的淘汰）
         */
        private boolean softValues = false;

        public int getInitialCapacity() { return initialCapacity; }
        public void setInitialCapacity(int initialCapacity) { this.initialCapacity = initialCapacity; }
        public long getMaximumSize() { return maximumSize; }
        public void setMaximumSize(long maximumSize) { this.maximumSize = maximumSize; }
        public Duration getExpireAfterAccess() { return expireAfterAccess; }
        public void setExpireAfterAccess(Duration expireAfterAccess) { this.expireAfterAccess = expireAfterAccess; }
        public boolean isSoftValues() { return softValues; }
        public void setSoftValues(boolean softValues) { this.softValues = softValues; }
    }

    public static class RedisConfig {
        /**
         * 是否启用Redis缓存，若classpath无redis依赖则强制不使用
         */
        private boolean enabled = true;

        /**
         * 全局默认过期时间
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration defaultExpiration = Duration.ofHours(1);

        /**
         * 是否允许缓存空值（防止缓存穿透）
         */
        private boolean cacheNullValues = true;

        /**
         * 是否使用键前缀
         */
        private boolean useKeyPrefix = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Duration getDefaultExpiration() { return defaultExpiration; }
        public void setDefaultExpiration(Duration defaultExpiration) { this.defaultExpiration = defaultExpiration; }
        public boolean isCacheNullValues() { return cacheNullValues; }
        public void setCacheNullValues(boolean cacheNullValues) { this.cacheNullValues = cacheNullValues; }
        public boolean isUseKeyPrefix() { return useKeyPrefix; }
        public void setUseKeyPrefix(boolean useKeyPrefix) { this.useKeyPrefix = useKeyPrefix; }
    }
}