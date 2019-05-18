package org.apollo.blog.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * 2018年10月27日 09:39
 */
@Component
@Slf4j
public class FutureManager {

    private CountDownLatch latch;
    /**
     * 在构造函数执行完之后执行
     */
    @PostConstruct
    public void init() {
        System.out.println("init-method//1在构造函数执行完之后执行");
    }

    /**
     * 在bean销毁之前执行
     */
    @PreDestroy
    public void destroy() {
        System.out.println("destory-method//2在bean销毁之前执行");
    }

}
