package org.apollo.blog.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apollo.blog.entity.User;
import org.apollo.blog.service.UserService;
import org.crazycake.shiro.RedisManager;
import org.apollo.blog.shiro.cache.RedisCacheManager;
import org.apollo.blog.shiro.filter.KickoutSessionControlFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apollo.blog.shiro.filter.ShiroLoginFilter;
import org.apollo.blog.shiro.session.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;

@Slf4j
@Configuration
public class ShiroConfig {

	@Resource
	private UserService userService;
	@Resource
	private KickoutSessionControlFilter kickoutSessionControlFilter;
	@Resource
	private RedisManager redisManager;
	@Resource
	private RedisSessionDAO redisSessionDAO;

	@Bean
	public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
		FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
		DelegatingFilterProxy proxy = new DelegatingFilterProxy();
		proxy.setTargetFilterLifecycle(true);
		proxy.setTargetBeanName("shiroFilter");
		filterRegistrationBean.setFilter(proxy);
		filterRegistrationBean.setDispatcherTypes(
				DispatcherType.REQUEST,
				DispatcherType.FORWARD,
				DispatcherType.INCLUDE,
				DispatcherType.ERROR);
		return filterRegistrationBean;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		//自定义拦截器
		LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
		//限制同一帐号同时在线的个数。
		filtersMap.put("authc", new ShiroLoginFilter());
		filtersMap.put("kickout", kickoutSessionControlFilter);
		shiroFilterFactoryBean.setFilters(filtersMap);

		shiroFilterFactoryBean.setSecurityManager(securityManager);

		shiroFilterFactoryBean.setLoginUrl("/login");
		shiroFilterFactoryBean.setSuccessUrl("/index");

		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		//配置记住我或认证通过可以访问的地址
		filterChainDefinitionMap.put("/", "user");
		filterChainDefinitionMap.put("/index", "user");
		//配置不会被拦截的连接  这里顺序判断
		//anon，所有的url都可以匿名访问
		//authc：所有url都必须认证通过才可以访问
		//user，配置记住我或者认证通过才能访问
		//logout，退出登录
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		filterChainDefinitionMap.put("/file/**", "anon");
		filterChainDefinitionMap.put("/api/**", "anon,kickout");
		filterChainDefinitionMap.put("/kickout", "anon,kickout");
		filterChainDefinitionMap.put("/getGifCode", "anon");
		filterChainDefinitionMap.put("/registered", "anon");
		filterChainDefinitionMap.put("/forgetpassword", "anon");
		filterChainDefinitionMap.put("/druid/**", "anon");
		//发送邮件不做限制
		filterChainDefinitionMap.put("/mail/**", "anon");
		//匿名访问静态资源
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/business/**", "anon");
		filterChainDefinitionMap.put("/fonts/**", "anon");
		filterChainDefinitionMap.put("/lib/**", "anon");
		filterChainDefinitionMap.put("/plugins/**", "anon");
		filterChainDefinitionMap.put("/languages/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/images/**", "anon");
		filterChainDefinitionMap.put("/layer/**", "anon");
		filterChainDefinitionMap.put("/**", "authc,kickout");
		filterChainDefinitionMap.put("/logout", "logout");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	@Bean
	public SimpleCookie rememberMeCookie() {
		//这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		//<!-- 记住我cookie生效时间30天 ,单位秒;-->
		simpleCookie.setMaxAge(259200);
		return simpleCookie;
	}

	@Bean
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());
		//rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
		cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
		return cookieRememberMeManager;
	}


	/**
	 * cacheManager 缓存 redis实现
	 * 使用的是shiro-redis开源插件
	 */
	public RedisCacheManager cacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager);
		return redisCacheManager;
	}

	@Bean
	public SecurityManager securityManager(@Qualifier("myShiroRealm") AuthorizingRealm myShiroRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 自定义缓存实现 使用redis
		securityManager.setCacheManager(cacheManager());
		// 自定义session管理 使用redis
		securityManager.setSessionManager(sessionManager());
		//注入记住我管理器;
		securityManager.setRememberMeManager(rememberMeManager());
		// 设置realm.
		securityManager.setRealm(myShiroRealm);
		return securityManager;
	}

	@Bean("myShiroRealm")
	public AuthorizingRealm myShiroRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
		AuthorizingRealm myShiroRealm = new AuthorizingRealm() {

			@Override
			protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
				log.info("认证配置");
				//获取用户的输入的账号.
				String username = (String) token.getPrincipal();
				//通过username从数据库中查找 User对象，
				User user = userService.findByUsername(username);
				if (user == null) {
					//没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
					return null;
				}
				return new SimpleAuthenticationInfo(
						//用户名
						user,
						//密码
						user.getPassword(),
						//salt=username+salt
						new MySimpleByteSource(user.getCredentialsSalt()),
						//realm name
						getName()
				);
			}

			@Override
			protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
				return new SimpleAuthorizationInfo();
			}
		};
		//设置加密规则
		myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
		myShiroRealm.setCachingEnabled(true);
		myShiroRealm.setAuthorizationCachingEnabled(true);
		myShiroRealm.setAuthenticationCachingEnabled(true);
		return myShiroRealm;
	}

	/**
	 * 需要与存储密码时的加密规则一致
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		//散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashAlgorithmName("md5");
		//散列的次数，比如散列两次，相当于 md5(md5(""));
		hashedCredentialsMatcher.setHashIterations(2);
		return hashedCredentialsMatcher;
	}

	/**
	 * 开启shiro aop注解支持.
	 * 使用代理方式;所以需要开启代码支持;
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * 添加ShiroDialect 为了在thymeleaf里使用shiro的标签的bean
	 */
	@Bean(name = "shiroDialect")
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	/**
	 * shiro session的管理
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		sessionManager.setSessionIdCookie(simpleCookie());
		sessionManager.setSessionDAO(redisSessionDAO);
		return sessionManager;
	}

	/**
	 * 这里需要设置一个cookie的名称  原因就是会跟原来的session的id值重复的
	 */
	@Bean
	public SimpleCookie simpleCookie() {
		return new SimpleCookie("_token");
	}
}
