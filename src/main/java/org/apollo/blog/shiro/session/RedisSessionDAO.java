package org.apollo.blog.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.entity.User;
import org.crazycake.shiro.RedisManager;
import org.apollo.blog.util.SerializeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Data
public class RedisSessionDAO extends AbstractSessionDAO {
	/**
	 * shiro-redis的session对象前缀
	 */
	@Resource
	private RedisManager redisManager;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * The Redis key prefix for the sessions
	 */
	private String keyPrefix = "apollo:shiro_redis_session:";

	private static String userkeyPrefix = "apollo:shiro_redis_user:";

	private static ConcurrentMap<String, HashSet<Serializable>> userMap = new ConcurrentHashMap<>();

	public ConcurrentMap<String, HashSet<Serializable>> getUserMap() {
		return userMap;
	}

	public void setUserMap(ConcurrentMap<String, HashSet<Serializable>> userMap) {
		RedisSessionDAO.userMap = userMap;
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		this.saveSession(session);
	}

	/**
	 * save session
	 *
	 * @param session
	 * @throws UnknownSessionException
	 */
	private void saveSession(Session session) throws UnknownSessionException {
		if (session == null || session.getId() == null) {
			log.error("session or session id is null");
			return;
		}
		try {
			Subject subject = SecurityUtils.getSubject();
			User user = (User) subject.getPrincipal();
			if (user != null) {
				String username = user.getUsername();
				HashSet<Serializable> hashSet = null;
				if (userMap.containsKey(username)) {
					hashSet = userMap.get(username);
				} else {
					hashSet = new HashSet<>();
				}
				hashSet.add(session.getId());
				userMap.put(username, hashSet);
				for (Serializable serializable : hashSet) {
					redisTemplate.opsForSet().add(getRedisStringKey(username), serializable.toString());
				}
			}
		} catch (Exception e) {
			log.error("关闭浏览器后，在打开浏览器出现问题");
		}

		byte[] key = getByteKey(session.getId());
		byte[] value = SerializeUtil.serialize(session);
		session.setTimeout(redisManager.getExpire() * 1000);
		this.redisManager.set(key, value, redisManager.getExpire());
	}

	public byte[] getRedisByteKey(String keyPrefix, Object o) {
		String preKey = keyPrefix + o;
		return preKey.getBytes();
	}

	public byte[] getRedisByteKey(Object o) {
		return getRedisByteKey(userkeyPrefix, o);
	}

	public String getRedisStringKey(String keyPrefix, Object o) {
		return keyPrefix + o;
	}

	public String getRedisStringKey(Object o) {
		return getRedisStringKey(userkeyPrefix, o);
	}

	@Override
	public void delete(Session session) {
		if (session == null || session.getId() == null) {
			log.error("session or session id is null");
			return;
		}
		redisManager.del(this.getByteKey(session.getId()));

	}

	public void delete(Serializable sessionId) {
		if (sessionId == null) {
			log.error("session id is null");
			return;
		}
		redisManager.del(this.getByteKey(sessionId));
	}

	public Session getSession(Serializable sessionId) {
		return doReadSession(sessionId);
	}

	@Override
	public Collection<Session> getActiveSessions() {
		Set<Session> sessions = new HashSet<Session>();

		Set<byte[]> keys = redisManager.keys(this.keyPrefix + "*");
		if (keys != null && keys.size() > 0) {
			for (byte[] key : keys) {
				Session s = (Session) SerializeUtil.deserialize(redisManager.get(key));
				sessions.add(s);
			}
		}

		return sessions;
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);
		this.assignSessionId(session, sessionId);
		this.saveSession(session);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		if (sessionId == null) {
			log.error("session id is null");
			return null;
		}

		return (Session) SerializeUtil.deserialize(redisManager.get(this.getByteKey(sessionId)));
	}

	/**
	 * 获得byte[]型的key
	 *
	 * @return
	 */
	private byte[] getByteKey(Serializable sessionId) {
		String preKey = this.keyPrefix + sessionId;
		return preKey.getBytes();
	}

	public RedisManager getRedisManager() {
		return redisManager;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;

		/**
		 * 初始化redisManager
		 */
		this.redisManager.init();
	}

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


}
