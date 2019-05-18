package org.apollo.blog.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.crazycake.shiro.RedisManager;
import org.apollo.blog.cache.CacheItemConfig;
import org.apollo.blog.cache.CustomizedRedisCacheManager;
import org.apollo.blog.cache.KryoRedisSerializer;
import org.apollo.blog.util.RSA.LoginRSAUtils;
import org.apollo.blog.util.RSA.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.expire-time}")
    private int expiretime;

    @Value("${spring.redis.password}")
    private String password;

    /**
     * 配置shiro redisManager
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        // 配置过期时间
        redisManager.setExpire(expiretime);
        redisManager.setPassword(password);
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /**
     * 功能描述:缓存数据时Key的生成器，可以依据业务和技术场景自行定制
     *
     * @author huangxin
     * @date 2019/4/8 16:54:00
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(":" + method.getName());
                if (params != null && params.length > 0) {
                    sb.append(":");
                }
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };

    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory, RedisTemplate<String, Object> redisTemplate) {

        RedisSerializer<Object> serializer = new KryoRedisSerializer(Object.class);
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
                // 设置缓存的默认过期时间，也是使用Duration设置
                .entryTtl(Duration.ofSeconds(60))
                // 不缓存空值
                .disableCachingNullValues();

		/*return RedisCacheManager.builder(factory)
				.cacheDefaults(config)
				.transactionAware()
				.build();*/

        //单独指定缓存对象的缓存有效时间及刷新时间
        List<CacheItemConfig> cacheItemConfigs = Lists.newArrayList();

        return new CustomizedRedisCacheManager(factory, config, redisTemplate, cacheItemConfigs);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> serializer = new KryoRedisSerializer(Object.class);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        //key序列化
        redisTemplate.setKeySerializer(stringSerializer);
        //value序列化
        redisTemplate.setValueSerializer(serializer);
        //Hash key序列化
        redisTemplate.setHashKeySerializer(stringSerializer);
        //Hash value序列化
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();

        LoginRSAUtils.setRedisTemplate(redisTemplate);
        RSAUtil.setRedisTemplate(redisTemplate);

        return redisTemplate;
    }
}