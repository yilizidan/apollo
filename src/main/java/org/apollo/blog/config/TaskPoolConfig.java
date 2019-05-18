package org.apollo.blog.config;

import org.apollo.blog.util.MdcTaskDecorator;
import org.apollo.blog.util.MdcThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiongfan
 * @description 线程池
 * @date 2018/11/16 11:10:16
 */
@EnableAsync(proxyTargetClass = true)
@Configuration
public class TaskPoolConfig {
	@Bean("taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new MdcThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(1000000);
		executor.setKeepAliveSeconds(60);
		executor.setThreadNamePrefix("MdcThreadExecutor-");
		executor.setTaskDecorator(new MdcTaskDecorator());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}


}