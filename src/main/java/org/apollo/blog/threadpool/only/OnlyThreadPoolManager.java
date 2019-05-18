package org.apollo.blog.threadpool.only;

import org.apollo.blog.threadpool.base.ThreadPoolFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OnlyThreadPoolManager {

    private enum SinglCease {
        /**
         * 初始化
         */
        INSTANCE;

        private OnlyThreadPoolManager instance;

        private SinglCease() {
            //在构造函数中完成实例化操作
            instance = new OnlyThreadPoolManager();
        }

        public OnlyThreadPoolManager getInstance() {
            return instance;
        }
    }

    /**
     * 单例模式
     */
    public static OnlyThreadPoolManager newInstance() {
        return SinglCease.INSTANCE.getInstance();
    }

    private static ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();


    /**
     * 管理工作线程的线程池
     */
    private static ExecutorService threadPool = ThreadPoolFactory.getThreadExecutor(0, 5, 0L, TimeUnit.SECONDS);


    public void addTasks(OnlyRunnable t) {
        t.setOnlyThreadPoolManager(this);
        if (concurrentLinkedQueue.isEmpty() || !concurrentLinkedQueue.contains(t.getT())) {
            threadPool.execute(t);
        }
    }

    public Future<?> addTasks2(OnlyRunnable t) {
        t.setOnlyThreadPoolManager(this);
        if (concurrentLinkedQueue.isEmpty() || !concurrentLinkedQueue.contains(t.getT())) {
            return threadPool.submit(t);
        }
        return null;
    }

    public void removeQueue(String value) {
        if (concurrentLinkedQueue.contains(value)) {
            concurrentLinkedQueue.remove(value);
        }
    }
}
