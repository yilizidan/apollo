package org.apollo.blog.threadpool.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 线程工厂
 * 2018年10月27日 09:27
 */
public class ThreadPoolFactory {
    /**
     * 线程池维护线程的最小数量。
     */
    private final static int CORE_POOL_SIZE = 5;
    /**
     * 线程池维护线程的最大数量。
     */
    private final static int MAX_POOL_SIZE = 20;
    /**
     * 线程池维护线程所允许的空闲时间。
     */
    private final static long KEEP_ALIVE_TIME = 0L;
    /**
     * 线程池维护线程所允许的空闲时间表示。
     */
    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    /**
     * 线程池所使用的缓冲队列大小。
     */
    private final static int WORK_QUEUE_SIZE = 2048;
    /**
     * 调度线程池维护线程的最小数量。
     */
    private final static int COREPOOL_SIZE = 20;
    /**
     * 循化线程 间隔时间
     */
    private final static long PERIOD = 100;

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("hx-pool-%d").build();

    public static ExecutorService getThreadExecutor() {
        return getThreadExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT);
    }

    public static ExecutorService getThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        final ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = new ConcurrentLinkedQueue<Runnable>();

        RejectedExecutionHandler handler = new RejectedExecutionHandler() {
            /**
             * 要创建的线程数量大于线程池的最大线程数的时候，新的任务就会被拒绝，就会调用这个接口里的这个方法。
             */
            @Override
            public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
                if (!concurrentLinkedQueue.contains(arg0)) {
                    System.out.println("放入队列中重新等待执行");
                    concurrentLinkedQueue.offer(arg0);
                }
            }
        };

        final ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(WORK_QUEUE_SIZE), namedThreadFactory, handler);
        //调度线程池
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(COREPOOL_SIZE);
        //delay时间后开始执行任务，并每隔period时间调用任务一次。
        final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!concurrentLinkedQueue.isEmpty() && ((ThreadPoolExecutor) executorService).getActiveCount() < MAX_POOL_SIZE) {
                    //从头部取出元素，并从队列里删除
                    Runnable t = concurrentLinkedQueue.poll();
                    if (t != null) {
                        executorService.execute(t);
                    }
                }
            }
        }, 0, PERIOD, TimeUnit.MILLISECONDS);

        return executorService;
    }

    public static ExecutorService getThreadExecutor(BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, workQueue, namedThreadFactory, handler);
    }

    public static ExecutorService getThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, namedThreadFactory, handler);
    }
}
