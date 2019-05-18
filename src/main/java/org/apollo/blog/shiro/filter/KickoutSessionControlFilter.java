package org.apollo.blog.shiro.filter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apollo.blog.entity.User;
import org.apollo.blog.shiro.session.RedisSessionDAO;
import org.crazycake.shiro.RedisManager;
import org.apollo.blog.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 作者 z77z
 * @date 创建时间：2017年3月5日 下午1:16:38
 * 1.读取当前登录用户名，获取在缓存中的sessionId队列
 * 2.判断队列的长度，大于最大登录限制的时候，按踢出规则
 * 将之前的sessionId中的session域中存入kickout：true，并更新队列缓存
 * 3.判断当前登录的session域中的kickout如果为true，
 * 想将其做退出登录处理，然后再重定向到踢出登录提示页面
 */
@Slf4j
@Component
public class KickoutSessionControlFilter extends AccessControlFilter {

	@Resource
	private RedisManager redisManager;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private RedisSessionDAO redisSessionDAO;

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		if (isLoginRequest(request, response)) {
			return true;
		}
		return false;
	}

	private byte[] getByteKey(Serializable sessionId) {
		String preKey = "apollo:shiro_redis_session:" + sessionId;
		return preKey.getBytes();
	}

	private PathMatcher pathMatcher = new AntPathMatcher();
	private static final List<String> EXCLUDE_PATTERNS = Collections
			.unmodifiableList(Lists.newArrayList(
					"/api/**"
			));

	@Value("${server.servlet.context-path}")
	private String contextPath;

	public boolean match(String targetPath) {
		return EXCLUDE_PATTERNS.stream().anyMatch(
				pattern -> pathMatcher.match(contextPath + pattern, targetPath)
		);
	}

	private static final List<String> ABOUT_PATTERNS = Collections
			.unmodifiableList(Lists.newArrayList(
					"/api/login",
					"/api/login",
					"/api/getGifCode",
					"/api/rememberMe",
					"/api/getPublicKey",
					"/api/registered",
					"/api/forgotpassword"
			));

	public boolean matchByabout(String targetPath) {
		return ABOUT_PATTERNS.stream().anyMatch(
				pattern -> pathMatcher.match(contextPath + pattern, targetPath)
		);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest rq = (HttpServletRequest) request;
		//静态资源请求直接通过
		if (!(match(rq.getRequestURI()) && !matchByabout(rq.getRequestURI()))) {
			return true;
		}
		Subject subject = getSubject(request, response);
		User user = (User) subject.getPrincipal();
		try {
			if (!subject.isAuthenticated() && !subject.isRemembered()) {
				//如果没有登录，直接进行之后的流程
				return true;
			} else if (user == null) {
				super.redirectToLogin(request, response);
				return false;
			}
		} catch (Exception e) {
			super.redirectToLogin(request, response);
			return false;
		}

		Session session = subject.getSession();
		Serializable sessionId = session.getId();

		byte[] key = getByteKey(session.getId());
		byte[] value = redisManager.get(key);
		if (value == null) {
			super.redirectToLogin(request, response);
			return false;
		}
		Session s = (Session) SerializeUtil.deserialize(value);

		if (((SimpleSession) s).isExpired()) {
			super.redirectToLogin(request, response);
			return false;
		}

		String username = user.getUsername();

		ConcurrentMap<String, HashSet<Serializable>> concurrentMap = redisSessionDAO.getUserMap();
		HashSet<Serializable> hashSet = concurrentMap.get(username);
		if (redisTemplate.hasKey(redisSessionDAO.getRedisStringKey(username))) {
			Set<Object> set = redisTemplate.opsForSet().members(redisSessionDAO.getRedisStringKey(username));
			if (hashSet == null || hashSet.isEmpty()) {
				hashSet = new HashSet<>();
			}
			for (Object b : set) {
				if (b != null) {
					hashSet.add(b.toString());
				}
			}
		}
		if (hashSet != null) {
			for (Serializable serializable : hashSet) {
				if (!sessionId.toString().equals(serializable.toString())) {
					redisManager.del(getByteKey(serializable));
				}
			}
		}

		if (concurrentMap == null || concurrentMap.isEmpty()) {
			concurrentMap = new ConcurrentHashMap<String, HashSet<Serializable>>();
		}
		hashSet = new HashSet<>();
		hashSet.add(sessionId);
		concurrentMap.put(username, hashSet);
		redisSessionDAO.setUserMap(concurrentMap);

		redisTemplate.delete(redisSessionDAO.getRedisStringKey(username));
		redisTemplate.opsForSet().add(redisSessionDAO.getRedisStringKey(username), sessionId.toString());
		return true;
	}
}
