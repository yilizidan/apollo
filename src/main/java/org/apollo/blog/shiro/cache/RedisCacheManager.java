package org.apollo.blog.shiro.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.crazycake.shiro.RedisManager;

@Slf4j
public class RedisCacheManager implements CacheManager {

    /**
     * fast lookup by name map
     */
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    private RedisManager redisManager;

    /**
     * The Redis key prefix for caches
     */
    private String keyPrefix = "apollo:shiro_redis_cache:";

    /**
     * Returns the Redis session keys
     * prefix.
     *
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets the Redis sessions key
     * prefix.
     *
     * @param keyPrefix The prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {

        Cache c = caches.get(name);

        if (c == null) {

            // initialize the Redis manager instance
            redisManager.init();

            // create a new cache instance
            c = new RedisCache<K, V>(redisManager, keyPrefix);

            // add it to the cache collection
            caches.put(name, c);
        }
        return c;
    }

    public void delCache(String name) {
        redisManager.init();
        new RedisCache(redisManager, keyPrefix).remove(name);
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

}