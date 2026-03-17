package com.agile.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(MultiLevelCacheProperties.class)
@ConditionalOnClass({Caffeine.class})
@ConditionalOnProperty(name = "sdk.cache.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({RedisAutoConfiguration.class, CacheAutoConfiguration.class})
public class MultiLevelCacheAutoConfiguration {

    /**
     * Caffeine 原生 Cache 实例（供二级缓存复用）
     */
    @Bean
    @ConditionalOnMissingBean(name = "caffeineNativeCache")
    public Cache<Object, Object> caffeineNativeCache(MultiLevelCacheProperties properties) {
        MultiLevelCacheProperties.CaffeineConfig cfg = properties.getCaffeine();
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .initialCapacity(cfg.getInitialCapacity())
                .maximumSize(cfg.getMaximumSize())
                .expireAfterAccess(cfg.getExpireAfterAccess());
        if (cfg.isSoftValues()) {
            builder.softValues();
        }
        return builder.build();
    }

    // ---------- Redis 可用且启用时：创建 RedisCacheManager ----------
    @Configuration
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnProperty(name = "sdk.cache.redis.enabled", havingValue = "true", matchIfMissing = true)
    public static class RedisCacheManagerConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "redisCacheManager")
        public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                                   MultiLevelCacheProperties properties) {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(properties.getRedis().getDefaultExpiration());
            // 根据配置决定是否允许缓存null值
            if (!properties.getRedis().isCacheNullValues()) {
                config = config.disableCachingNullValues();
            }
            // 前缀控制保留
            if (!properties.getRedis().isUseKeyPrefix()) {
                config = config.disableKeyPrefix();
            }
            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(config)
                    .build();
        }
    }

    // ---------- 降级方案：Redis 不可用或禁用时，纯 Caffeine ----------
    @Configuration
    @ConditionalOnMissingBean(name = "redisCacheManager")
    @ConditionalOnProperty(name = "sdk.cache.redis.enabled", havingValue = "false", matchIfMissing = true)
    public static class FallbackCacheConfiguration {

        @Bean
        @Primary
        @ConditionalOnMissingBean(CacheManager.class)
        public CaffeineCacheManager caffeineCacheManager(MultiLevelCacheProperties properties) {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager();
            MultiLevelCacheProperties.CaffeineConfig cfg = properties.getCaffeine();

            Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                    .initialCapacity(cfg.getInitialCapacity())
                    .maximumSize(cfg.getMaximumSize())
                    .expireAfterAccess(cfg.getExpireAfterAccess());
            if (cfg.isSoftValues()) {
                caffeineBuilder.softValues();
            }

            cacheManager.setCaffeine(caffeineBuilder);
            cacheManager.setAllowNullValues(properties.getRedis().isCacheNullValues()); // 复用属性
            return cacheManager;
        }
    }

    // ---------- 二级缓存模式：RedisCacheManager 存在时，构建 MultiLevelCacheManager ----------
    @Configuration
    @ConditionalOnBean(name = "redisCacheManager")
    @ConditionalOnProperty(name = "sdk.cache.redis.enabled", havingValue = "true", matchIfMissing = true)
    public static class MultiLevelConfiguration {

        @Bean
        @Primary
        @ConditionalOnMissingBean(CacheManager.class)
        public MultiLevelCacheManager multiLevelCacheManager(Cache<Object, Object> caffeineNativeCache,
                                                             CacheManager redisCacheManager,
                                                             MultiLevelCacheProperties properties) {
            return new MultiLevelCacheManager(caffeineNativeCache, redisCacheManager, properties);
        }
    }
}

