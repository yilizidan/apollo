package org.apollo.blog.cache;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义的redis缓存管理器
 * 支持方法上配置过期时间
 * 支持热加载缓存：缓存即将过期时主动刷新缓存
 * Created by jiang on 2017/3/5.
 */
public class CustomizedRedisCacheManager extends RedisCacheManager {

	private RedisCacheWriter redisCacheWriter;
	private RedisCacheConfiguration defaultRedisCacheConfiguration;
	private RedisTemplate<String, Object> redisTemplate;

	public CustomizedRedisCacheManager(
			RedisConnectionFactory connectionFactory,
			RedisCacheConfiguration defaultCacheConfig,
			RedisTemplate<String, Object> redisOperations,
			List<CacheItemConfig> cacheItemConfigList) {

		this(
				RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
				defaultCacheConfig,
				cacheItemConfigList.stream()
						.collect(Collectors.toMap(CacheItemConfig::getName, cacheItemConfig -> {
							return RedisCacheConfiguration
									.defaultCacheConfig()
									.entryTtl(Duration.ofSeconds(cacheItemConfig.getExpiryTimeSecond()))
									.prefixKeysWith(cacheItemConfig.getName());
						}))
		);
		this.redisTemplate = redisOperations;
		CacheContainer.init(cacheItemConfigList);

	}

	public CustomizedRedisCacheManager(
			RedisCacheWriter redisCacheWriter
			, RedisCacheConfiguration redisCacheConfiguration,
			Map<String, RedisCacheConfiguration> redisCacheConfigurationMap) {
		super(redisCacheWriter, redisCacheConfiguration, redisCacheConfigurationMap);
		this.redisCacheWriter = redisCacheWriter;
		this.defaultRedisCacheConfiguration = redisCacheConfiguration;
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (null == cache) {
			return cache;
		}
		return new CustomizedRedisCache(
				name,
				this.redisCacheWriter,
				this.defaultRedisCacheConfiguration,
				this.redisTemplate
		);
	}
}
