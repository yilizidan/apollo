package org.apollo.blog.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.MethodInvoker;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 手动刷新缓存实现类
 */
@Component
@Slf4j
public class CacheSupportImpl implements CacheSupport, InvocationRegistry {

	@Resource
	private KeyGenerator keyGenerator;
	/**
	 * 记录容器与所有执行方法信息
	 */
	private Map<String, Set<CachedInvocation>> cacheToInvocationsMap;

	@Autowired
	private CacheManager cacheManager;

	private void refreshCache(CachedInvocation invocation, String cacheName) {

		boolean invocationSuccess;
		Object computed = null;
		try {
			computed = invoke(invocation);
			invocationSuccess = true;
		} catch (Exception ex) {
			invocationSuccess = false;
		}
		if (invocationSuccess) {
			if (cacheToInvocationsMap.get(cacheName) != null) {
				cacheManager.getCache(cacheName).put(invocation.getKey(), computed);
			}
		}
	}

	private Object invoke(CachedInvocation invocation)
			throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final MethodInvoker invoker = new MethodInvoker();
		invoker.setTargetObject(invocation.getTargetBean());
		invoker.setArguments(invocation.getArguments());
		invoker.setTargetMethod(invocation.getTargetMethod().getName());
		invoker.prepare();
		return invoker.invoke();
	}


	@PostConstruct
	public void initialize() {
		cacheToInvocationsMap = new ConcurrentHashMap<String, Set<CachedInvocation>>(cacheManager.getCacheNames().size());
		for (final String cacheName : cacheManager.getCacheNames()) {
			cacheToInvocationsMap.put(cacheName, new CopyOnWriteArraySet<CachedInvocation>());
		}
	}

	@Override
	public void registerInvocation(Object targetBean, Method targetMethod, Object[] arguments, Set<String> annotatedCacheNames) {
		Object key = keyGenerator.generate(targetBean, targetMethod, arguments);
		final CachedInvocation invocation = new CachedInvocation(key, targetBean, targetMethod, arguments);
		for (final String cacheName : annotatedCacheNames) {
			String[] cacheParams=cacheName.split("#");
			String realCacheName = cacheParams[0];
			if(!cacheToInvocationsMap.containsKey(realCacheName)) {
				this.initialize();
			}
			cacheToInvocationsMap.get(realCacheName).add(invocation);
		}
	}

	@Override
	public void refreshCache(String cacheName) {
		this.refreshCacheByKey(cacheName,null);
	}

	@Override
	public void refreshCacheByKey(String cacheName, String cacheKey) {
		if (cacheToInvocationsMap.get(cacheName) != null) {
			for (final CachedInvocation invocation : cacheToInvocationsMap.get(cacheName)) {
				if(!StringUtils.isBlank(cacheKey)&&invocation.getKey().toString().equals(cacheKey)) {
					refreshCache(invocation, cacheName);
				}
			}
		}
	}

}
