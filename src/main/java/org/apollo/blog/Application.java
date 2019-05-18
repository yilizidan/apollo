package org.apollo.blog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching //启用缓存，这个注解很重要；
@EnableTransactionManagement // 开启事务管理
@MapperScan("org.apollo.blog.mapper")
@EnableRedisHttpSession //开启spring session支持  实现session共享
@Slf4j
public class Application {
	public static void main(String[] args) {
		new SpringApplicationBuilder()
				.sources(Application.class)
				//禁止添加命令行参数
				.addCommandLineProperties(false)
				.run(args);
		log.info("ヾ(◍°∇°◍)ﾉﾞ    apollo启动成功      ヾ(◍°∇°◍)ﾉﾞ");
	}
}

