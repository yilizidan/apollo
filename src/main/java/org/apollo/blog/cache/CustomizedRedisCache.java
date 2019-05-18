package org.apollo.blog.cache;

import org.apollo.blog.cache.helper.ApplicationContextHelper;
import org.apollo.blog.cache.helper.ThreadTaskHelper;
import org.apollo.blog.util.lock.RedisAtomicClient;
import org.apollo.blog.util.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 自定义的redis缓存
 * Created by jiang on 2017/3/5.
 */
public class CustomizedRedisCache extends RedisCache {

	private static final Logger logger = LoggerFactory.getLogger(CustomizedRedisCache.class);

	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private RedisAtomicClient redisAtomicClient;

	private CacheSupport getCacheSupport() {
		return ApplicationContextHelper.getApplicationContext().getBean(CacheSupport.class);
	}

	public CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig, RedisTemplate<String, Object> redisTemplate) {
		super(name, cacheWriter, cacheConfig);
		this.redisTemplate = redisTemplate;
		this.redisAtomicClient = new RedisAtomicClient(redisTemplate);
	}


	@Override
	public ValueWrapper get(final Object key) {
		ValueWrapper valueWrapper = super.get(key);
		if (null != valueWrapper) {
			CacheItemConfig cacheItemConfig = CacheContainer.getCacheItemConfigByCacheName(key.toString());
			long preLoadTimeSecond = cacheItemConfig.getPreLoadTimeSecond();
			String cacheKey = this.createCacheKey(key);
			Long ttl = this.redisTemplate.getExpire(cacheKey);
			if (null != ttl && ttl <= preLoadTimeSecond) {
				logger.info("load object from cache with key:{} ttl:{} preloadSecondTime:{}", cacheKey, ttl, preLoadTimeSecond);
				if (ThreadTaskHelper.hasRunningRefreshCacheTask(cacheKey)) {
					logger.info("do not need to refresh");
				} else {
					RedisLock lock = redisAtomicClient.getLock(cacheKey, 10);
					if (lock != null) {
						logger.info("add task {}", key);
						ThreadTaskHelper.run(new Runnable() {
							@Override
							public void run() {
								try {
									if (ThreadTaskHelper.hasRunningRefreshCacheTask(cacheKey)) {
										logger.info("key:{} do not need to refresh", cacheKey);
									} else {
										logger.info("refresh key:{}", cacheKey);
										CustomizedRedisCache.this.getCacheSupport().refreshCacheByKey(CustomizedRedisCache.super.getName(), key.toString());
										ThreadTaskHelper.removeRefreshCacheTask(cacheKey);
									}

								} catch (Exception e) {
									logger.error("ThreadTaskHelper.run Exception", e);
								}
							}
						});
					}
				}
			}
		}
		return valueWrapper;
	}
}
