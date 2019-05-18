package org.apollo.blog.util.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Resource(name="stringRedisTemplate")
 * private RedisAtomicClient redisAtomicClient;
 *
 * 提供Redis一些不直接支持的原子性的操作,很多实现采用了lua脚本
 * Created by zhengjy on 2017/3/6.
 */
@Component
public class RedisAtomicClient {
	
    private static final Logger logger = LoggerFactory.getLogger(RedisAtomicClient.class);
 
    private final RedisTemplate redisTemplate;
    
    private final StringRedisTemplate stringRedisTemplate;
 
    private static final String INCR_BY_WITH_TIMEOUT = "local v;" +
            " v = redis.call('incrBy',KEYS[1],ARGV[1]);" +
            "if tonumber(v) == 1 then\n" +
            "    redis.call('expire',KEYS[1],ARGV[2])\n" +
            "end\n" +
            "return v";
    private static final String COMPARE_AND_DELETE =
            "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
            "then\n" +
            "    return redis.call('del',KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";
 
    public RedisAtomicClient(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = new StringRedisTemplate();
        this.stringRedisTemplate.setConnectionFactory(redisTemplate.getConnectionFactory());
        this.stringRedisTemplate.afterPropertiesSet();
    }
 
    /**
     * 计数器,支持设置失效时间，如果key不存在，则调用此方法后计数器为1（本方法使用string序列化方式）
     * @param key
     * @param delta 可以为负数
     * @param timeout 缓存失效时间
     * @param timeUnit 缓存失效时间的单位
     * @return
     */
    public Long incrBy(String key, long delta, long timeout, TimeUnit timeUnit){
        List<String> keys = new ArrayList<>();
        keys.add(key);
        long timeoutSeconds = TimeUnit.SECONDS.convert(timeout, timeUnit);
        String[] args = new String[2];
        args[0] = String.valueOf(delta);
        args[1] = String.valueOf(timeoutSeconds);
        Object currentVal = stringRedisTemplate.execute(new DefaultRedisScript<>(INCR_BY_WITH_TIMEOUT, String.class), keys, args);
 
        if(currentVal instanceof Long){
            return (Long)currentVal;
        }
        return Long.valueOf((String)currentVal);
    }
 
    /**
     * 获取redis的分布式锁，内部实现使用了redis的setnx。只会尝试一次，如果锁定失败返回null，如果锁定成功则返回RedisLock对象，调用方需要调用RedisLock.unlock()方法来释放锁.
     * <br/>使用方法：
     * <pre>
     * RedisLock lock = redisAtomicClient.getLock(key, 2);
     * if(lock != null){
     *      try {
     *          //lock succeed, do something
     *      }finally {
     *          lock.unlock();
     *      }
     * }
     * </pre>
     * 由于RedisLock实现了AutoCloseable,所以可以使用更简介的使用方法:
     * <pre>
     *  try(RedisLock lock = redisAtomicClient.getLock(key, 2)) {
     *      if (lock != null) {
     *          //lock succeed, do something
     *      }
     *  }
     * </pre>
     * @param key 要锁定的key
     * @param expireSeconds key的失效时间
     * @return 获得的锁对象（如果为null表示获取锁失败），后续可以调用该对象的unlock方法来释放锁.
     */
    public RedisLock getLock(final String key, long expireSeconds){
        return getLock(key, expireSeconds, 0, 0);
    }
 
 
    /**
     * 删除锁
     * @param key
     * @return
     */
    public void unLock(final String key){
    	stringRedisTemplate.delete(key);
    }
    
    /**
     * 获取redis的分布式锁，内部实现使用了redis的setnx。如果锁定失败返回null，如果锁定成功则返回RedisLock对象，调用方需要调用RedisLock.unlock()方法来释放锁
     * <br/>
     * <span style="color:red;">此方法在获取失败时会自动重试指定的次数,由于多次等待会阻塞当前线程，请尽量避免使用此方法</span>
     *
     * @param key 要锁定的key
     * @param expireSeconds key的失效时间
     * @param maxRetryTimes 最大重试次数,如果获取锁失败，会自动尝试重新获取锁；
     * @param retryIntervalTimeMillis 每次重试之前sleep等待的毫秒数
     * @return 获得的锁对象（如果为null表示获取锁失败），后续可以调用该对象的unlock方法来释放锁.
     */
    public RedisLock getLock(String key, final long expireSeconds, int maxRetryTimes, long retryIntervalTimeMillis){
        
    		final String value = key.hashCode()+"";
    		key = "lock:"+key;
        int maxTimes = maxRetryTimes + 1;
        for(int i = 0;i < maxTimes; i++) {
            String status = "";
            if (stringRedisTemplate.opsForValue().setIfAbsent(key,value)) {
                stringRedisTemplate.expire(key,expireSeconds,TimeUnit.SECONDS);
                status = "OK";
            }
            //抢到锁
            if ("OK".equals(status)) {
                return new RedisLockInner(stringRedisTemplate, key, value);
            }
 
            if(retryIntervalTimeMillis > 0) {
                try {
                    Thread.sleep(retryIntervalTimeMillis);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if(Thread.currentThread().isInterrupted()){
                break;
            }
        }
 
        return null;
    }
 
    private class RedisLockInner implements RedisLock{
        private StringRedisTemplate stringRedisTemplate;
        private String key;
        private String expectedValue;
 
        protected RedisLockInner(StringRedisTemplate stringRedisTemplate, String key, String expectedValue){
            this.stringRedisTemplate = stringRedisTemplate;
            this.key = key;
            this.expectedValue = expectedValue;
        }
 
        /**
         * 释放redis分布式锁
         */
        @Override
        public void unlock(){
            List<String> keys = Collections.singletonList(key);
            stringRedisTemplate.execute(new DefaultRedisScript<>(COMPARE_AND_DELETE, String.class), keys, expectedValue);
        }
 
        @Override
        public void close() throws Exception {
            this.unlock();
        }
    }
}
