package org.apollo.blog.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huangxin
 * @description 拦截器
 * @date 2019/4/8 14:15:08
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

	/**
	 * 功能描述:方法执行前
	 *
	 * @author huangxin
	 * @date 2019/4/8 14:18:00
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	/**
	 * 功能描述:方法执行后
	 *
	 * @author huangxin
	 * @date 2019/4/8 14:17:00
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws IOException {
	}

	/**
	 * 功能描述:页面渲染前
	 *
	 * @author huangxin
	 * @date 2019/4/8 14:17:00
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

	}
}