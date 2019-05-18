package org.apollo.blog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.common.LogAnnotation;
import org.apollo.blog.entity.User;
import org.apollo.blog.service.RzService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.apollo.blog.entity.Rz;
import org.apollo.blog.util.IPUtils;
import org.apollo.blog.util.LocalDateTimeUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@Order(2)
public class LoginAspect {

	@Resource
	private RzService rzService;

	/**
	 * 切入点设置到自定义的log注解上
	 */
	@Pointcut("@annotation(org.apollo.blog.common.LogAnnotation)")
	private void pointCutMethod() {
	}


	/**
	 * 使用上面定义的切入点
	 */
	@After("pointCutMethod()")
	public void recordLog(JoinPoint joinPoint) throws Exception {
		Long start = System.currentTimeMillis();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		if (user != null) {
			try {
				String ip = IPUtils.getIpAdrress(request);
				Map<String, String> map = getLogMark(joinPoint);
				Rz rz = Rz.builder()
						.userid(user.getId())
						.loginTime(LocalDateTimeUtil.getTimestampOfDateTime(LocalDateTime.now()))
						.loginType(1)
						.ip(ip)
						.action(map.get("action"))
						.targetType(map.get("targetType"))
						.remark(map.get("remark"))
						.build();
				rzService.save(rz);
			} catch (ClassNotFoundException e) {
				log.error("插入日志异常", e.getMessage());
			}
		}
		Long end = System.currentTimeMillis();
		log.info("记录日志消耗时间:" + (end - start) / 1000);
	}

	private Map<String, String> getLogMark(JoinPoint joinPoint) throws ClassNotFoundException {
		Map<String, String> map = new HashMap<>();
		String methodName = joinPoint.getSignature().getName();
		String targetName = joinPoint.getTarget().getClass().getName();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
				map.put("targetType", logAnnotation.targetType());
				map.put("action", logAnnotation.action());
				map.put("remark", logAnnotation.remark());
			}
		}
		return map;
	}
}