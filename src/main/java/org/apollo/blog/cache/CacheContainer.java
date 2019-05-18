package org.apollo.blog.cache;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by jiangmin on 2018/4/7.
 */
public class CacheContainer {

	private static final String DEFAULT_CACHE_NAME = "default";

	private static final Map<String, CacheItemConfig> CACHE_CONFIG_HOLDER = new ConcurrentHashMap<String, CacheItemConfig>();

	static {
		//默认的缓存有效期和刷新时间
		CACHE_CONFIG_HOLDER.put(DEFAULT_CACHE_NAME, new CacheItemConfig(DEFAULT_CACHE_NAME, 300, 5L));
	}

	public static void init(List<CacheItemConfig> cacheItemConfigs) {

		if (CollectionUtils.isEmpty(cacheItemConfigs)) {
			return;
		}
		cacheItemConfigs.forEach(cacheItemConfig -> {
			CACHE_CONFIG_HOLDER.put(cacheItemConfig.getName(), cacheItemConfig);
		});

	}

	public static CacheItemConfig getCacheItemConfigByCacheName(String cacheName) {
		if (CACHE_CONFIG_HOLDER.containsKey(cacheName)) {
			return CACHE_CONFIG_HOLDER.get(cacheName);
		}
		return CACHE_CONFIG_HOLDER.get(DEFAULT_CACHE_NAME);
	}

	public static List<CacheItemConfig> getCacheItemConfigs() {
		return CACHE_CONFIG_HOLDER.values().stream().filter(new Predicate<CacheItemConfig>() {
			@Override
			public boolean test(CacheItemConfig cacheItemConfig) {
				return !cacheItemConfig.getName().equals(DEFAULT_CACHE_NAME);
			}
		}).collect(Collectors.toList());
	}
}
