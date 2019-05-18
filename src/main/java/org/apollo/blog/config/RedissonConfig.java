package org.apollo.blog.config;

import org.apache.commons.lang3.StringUtils;
import org.apollo.blog.config.properties.RedissonProperties;
import org.apollo.blog.util.lock.DistributedLocker;
import org.apollo.blog.util.lock.RedissLockUtil;
import org.apollo.blog.util.lock.RedissonDistributedLocker;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author huangxin
 * @description Redisson配置类
 * @date 2019/4/1 14:05:01
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

    @Resource
    private RedissonProperties redssionProperties;

    /**
     * 单机模式自动装配
     */
    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        config.setCodec(new KryoCodec()).setKeepPubSubOrder(true);
        SingleServerConfig serverConfig = config.useSingleServer()
                .setDatabase(0)
                .setAddress(redssionProperties.getAddress())
                .setTimeout(redssionProperties.getTimeout())
                .setConnectionPoolSize(redssionProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redssionProperties.getConnectionMinimumIdleSize());

        if (StringUtils.isNotBlank(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }

        return Redisson.create(config);
    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     */
    @Bean
    public DistributedLocker distributedLocker(RedissonClient redissonClient) {
        RedissonDistributedLocker locker = new RedissonDistributedLocker();
        locker.setRedissonClient(redissonClient);
        RedissLockUtil.setLocker(locker);
        return locker;
    }
}