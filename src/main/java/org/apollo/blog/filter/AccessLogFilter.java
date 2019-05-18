package org.apollo.blog.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apollo.blog.util.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author huangxin
 * @description 访问日志 过虑
 * @date 2019/4/8 10:15:08
 */
@Component
@Slf4j
public class AccessLogFilter {

	private PathMatcher pathMatcher = new AntPathMatcher();
	private static final List<String> EXCLUDE_PATTERNS = Collections
			.unmodifiableList(Lists.newArrayList(
					"/",
					"/null/**",
					"/druid/**",
					"/swagger**",
					"/swagger**/**",
					"/v2/**",
					"/csrf",
					"/webjars/**",
					"/fonts/**",
					"/images/**",
					"/css/**",
					"/business/**",
					"/languages/**",
					"/js/**",
					"/file/**"
			));

	@Value("${server.servlet.context-path}")
	private String contextPath;

	public boolean match(String targetPath) {
		return EXCLUDE_PATTERNS.stream().anyMatch(
				pattern -> pathMatcher.match(contextPath + pattern, targetPath)
		);
	}

	@Bean("myLogFilter")
	public Filter accessLogFilter() {
		return new Filter() {
			@Override
			public void init(FilterConfig filterConfig) {
				log.info("AccessLogFilter init");
			}

			@Override
			public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				long startTime = System.currentTimeMillis();
				HttpServletRequest request = (HttpServletRequest) req;
				String clientIp = HttpUtils.getCliectIp((HttpServletRequest) request);
				// 请求的路径
				String path = request.getRequestURI();
				boolean excPath = match(path);
				// 忽略的IP请求
				if (excPath || HttpUtils.isIgnore(clientIp)) {
					chain.doFilter(req, response);
				} else {
					ResponseWrapper responeWrapper = new ResponseWrapper((HttpServletResponse) response);
					ServletRequest requestWrapper = null;
					// 创建请求实体
					AccessLogger logger = new AccessLogger();
					// 获取请求参数信息
					String paramData = null;
					String contentType = request.getContentType();
					if (StringUtils.isNoneBlank(contentType)
							&& contentType.toLowerCase().contains("application/json")
							&& "POST".equalsIgnoreCase(request.getMethod())) {
						requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
						paramData = HttpHelper.getBodyString(requestWrapper);
					} else {
						paramData = JSON.toJSONString(request.getParameterMap(),
								SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);
					}
					// 设置客户端ip
					logger.setIp(clientIp);
					// 请求时间
					logger.setAccessTime(startTime);
					//
					logger.setTraceId(UUID.randomUUID().toString());

					// 设置请求方法
					logger.setMethod(request.getMethod());
					// 设置请求类型
					logger.setType(HttpUtils.getRequestType(request));
					// 设置请求参数的json字符串
					logger.setParamData(paramData);
					// 设置请求地址
					logger.setUrl(path);
					// 获取请求的sessionId
					logger.setSessionId(request.getSession().getId());

					logger.setHttpContentType(request.getContentType());

					// 设置请求开始时间
					if (requestWrapper != null) {
						chain.doFilter(requestWrapper, responeWrapper);
					} else {
						chain.doFilter(request, responeWrapper);
					}

					String resultData = responeWrapper.getResponseData(response.getCharacterEncoding());
					response.getOutputStream().write(resultData.getBytes());

					// 获取请求错误码
					int status = responeWrapper.getStatus();
					// 当前时间
					long currentTime = System.currentTimeMillis();
					// 设置请求时间差
					logger.setUseTime(Integer.valueOf(String.valueOf((currentTime - startTime))));
					// 设置返回时间
					logger.setReturnTime(String.valueOf(currentTime));
					// 设置返回错误码
					logger.setHttpStatusCode(String.valueOf(status));

					if (StringUtils.isNotBlank(resultData) && resultData.length() > 1024) {
						// 设置返回值
						logger.setReturnData("##result data too long");
					} else {
						// 设置返回值
						logger.setReturnData(resultData);
					}
					log.info("HTTP AccessLogFilter请求：{}", logger.toString());
				}
			}

			@Override
			public void destroy() {
				log.info("AccessLogFilter destroy");
			}
		};
	}

	@Bean
	public DelegatingFilterProxyRegistrationBean registrateLogFilter() {
		DelegatingFilterProxyRegistrationBean bean = new DelegatingFilterProxyRegistrationBean("myLogFilter");
		bean.addUrlPatterns("/*");
		bean.setOrder(1);
		return bean;
	}
}