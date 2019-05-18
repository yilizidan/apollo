package org.apollo.blog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author huangxin
 * @description Redisson配置实例类
 * @date 2019/4/1 14:04:01
 */
@ConfigurationProperties(prefix = "redisson")
@Data
public class RedissonProperties {
	private int timeout = 3000;

	private String address;

	private String password;

	private int connectionPoolSize = 64;

	private int connectionMinimumIdleSize = 10;

	private int slaveConnectionPoolSize = 250;

	private int masterConnectionPoolSize = 250;

	private String[] sentinelAddresses;

	private String masterName;
}