package org.apollo.blog.threadpool.base;


import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.threadpool.branch.ThreadPoolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class BaseRunnable<T> implements Runnable {

    private T t = null;

    @Autowired
    private ThreadPoolManager threadPoolManager;

    public T getT() {
        return t;
    }

    protected BaseRunnable() {
        this.t = init();
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            log.error("BaseRunnable:" + e.getMessage(), e);
        } finally {
            if (t != null) {
                threadPoolManager.removeCacheSet(t);
            }
        }
    }

    protected abstract T init();

    protected abstract void execute() throws Exception;
}
