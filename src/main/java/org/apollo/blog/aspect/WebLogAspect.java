package org.apollo.blog.aspect;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 拦截顺序Filter->Interceptor->ControllerAdvice->Aspect->Controller 发生异常的时候是反向的传递.
 * 过滤器->拦截器->controller的增强->切面->控制器
 * ControllerAdvice处理的异常不会传递到Interceptor的afterCompletion方法之中
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class WebLogAspect {

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 两个..代表所有子目录，最后括号里的两个..代表所有参数
     */
    @Pointcut("execution(public * org.apollo.blog.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 拦截方法执行前的动作
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

        startTime.set(System.currentTimeMillis());

        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //记录下请求内容
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 方法执行前后的动作，注意要有返回值
     */
    @Around("webLog()")
    public Object around(JoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return ((ProceedingJoinPoint) joinPoint).proceed();
        } catch (Throwable throwable) {
            long end = System.currentTimeMillis();
            log.error("around : " + joinPoint + "\tUse time:" + (end - start) + "ms with exception " + throwable.getMessage(), throwable);
            throw new Exception(throwable);
        }
    }

    /**
     * 拦截方法执行完成之后的动作
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        //处理完请求，返回内容
        log.info("RESPONSE : " + ret);
        log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }
}

