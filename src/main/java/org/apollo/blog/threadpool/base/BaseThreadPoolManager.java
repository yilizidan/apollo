package org.apollo.blog.threadpool.base;

import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.Set;
import java.util.concurrent.*;

public abstract class BaseThreadPoolManager implements BeanFactoryAware {

    /**
     * 用于从IOC里取对象
     */
    protected BeanFactory factory;
    /**
     * 线程池维护线程的最小数量。
     */
    protected int CORE_POOL_SIZE = 5;
    /**
     * 线程池维护线程的最大数量。
     */
    protected int MAX_POOL_SIZE = 20;
    /**
     * 线程池维护线程所允许的空闲时间。
     */
    protected long KEEP_ALIVE_TIME = 0L;
    /**
     * 线程池维护线程所允许的空闲时间表示。
     */
    protected TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    /**
     * 调度线程池维护线程的最小数量。
     */
    protected int COREPOOL_SIZE = 20;
    /**
     * 线程池所使用的缓冲队列大小
     */
    protected int WORK_QUEUE_SIZE = 1024;

    /**
     * 循化线程 间隔时间
     */
    protected long PERIOD = 1;

    @Override
    public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.factory = factory;
    }

    protected void setCorePoolSize(int corePoolSize) {
        this.CORE_POOL_SIZE = corePoolSize;
    }

    protected void setMaxPoolSize(int maxPoolSize) {
        this.MAX_POOL_SIZE = maxPoolSize;
    }

    protected void setKeepAliveTime(long keepAliveTime) {
        this.KEEP_ALIVE_TIME = keepAliveTime;
    }

    protected void setTimeUnit(TimeUnit timeUnit) {
        this.TIME_UNIT = timeUnit;
    }

    protected void setSchedulerCorepoolSize(int corepoolSize) {
        this.COREPOOL_SIZE = corepoolSize;
    }

    protected void setWorkQueueSize(int workQueueSize) {
        this.WORK_QUEUE_SIZE = workQueueSize;
    }

    protected void setPERIOD(long PERIOD) {
        this.PERIOD = PERIOD;
    }

    /**
     * 用于储存在队列中的订单,防止重复提交,在真实场景中，可用redis代替 验证重复
     */
    protected Set<Object> cacheSet = Sets.newConcurrentHashSet();


    /**
     * 订单的缓冲队列,当线程池满了，则将订单存入到此缓冲队列
     */
    protected ConcurrentLinkedQueue<Runnable> msgQueue = new ConcurrentLinkedQueue<Runnable>();
    /**
     * 当线程池的容量满了，执行下面代码，将订单存入到缓冲队列
     */
    protected RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            BaseRunnable b = (BaseRunnable) r;
            if (!cacheSet.contains(b.getT())) {
                msgQueue.offer(r);
            }
        }
    };


    /**
     * 管理工作线程的线程池
     * corePoolSize
     * 核心线程数，默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。
     * maximumPoolSize
     * 线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。
     * keepAliveTime
     * 非核心线程的闲置超时时间，超过这个时间就会被回收。
     * unit
     * 指定keepAliveTime的单位，如TimeUnit.SECONDS。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。
     * workQueue
     * 线程池中的任务队列. 常用的有三种队列，SynchronousQueue,LinkedBlockingDeque,ArrayBlockingQueue。
     * threadFactory
     * 线程工厂，提供创建新线程的功能。ThreadFactory是一个接口，只有一个方法
     */
    protected ThreadPoolExecutor threadPool = (ThreadPoolExecutor) ThreadPoolFactory.getThreadExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    /**
     * 将任务加入线程池
     */
    public void addTasks(BaseRunnable t) {
        addTasks(t, true);
    }

    /**
     * 将任务加入线程池
     */
    public void addTasks(BaseRunnable t, boolean join) {
        if (join) {
            //防止添加相同对象（包含重载对象）
            if (cacheSet.contains(t.getT())) {
                cacheSet.add(t.getT());
                threadPool.execute(t);
            }
        } else {
            threadPool.execute(t);
        }
    }

    /**
     * 线程池的定时任务----> 称为(调度线程池)。此线程池支持 定时以及周期性执行任务的需求。
     */
    protected ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * 检查(调度线程池)，每秒执行一次，查看缓冲队列是否有记录，则重新加入到线程池
     */
    protected ScheduledFuture scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            //判断缓冲队列是否存在记录
            if (!msgQueue.isEmpty()) {
                //当线程池的队列容量少于WORK_QUEUE_SIZE，则开始把缓冲队列的订单 加入到 线程池
                if (threadPool.getQueue().size() < WORK_QUEUE_SIZE) {
                    Runnable t = msgQueue.poll();
                    threadPool.execute(t);
                }
            }
        }
    }, 0, PERIOD, TimeUnit.SECONDS);

    public void removeCacheSet(Object o) {
        if (cacheSet.contains(o)) {
            cacheSet.remove(o);
        }
    }

    /**
     * 获取消息缓冲队列
     */
    public ConcurrentLinkedQueue getQueueSize() {
        return msgQueue;
    }

    /**
     * 不代表线程池中未执行的
     */
    public int getActionPoolSize() {
        return threadPool.getActiveCount();
    }

    /**
     * 终止订单线程池+调度线程池
     * 阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的。
     */
    public void shutdown() {
        //true表示如果定时任务在执行，立即中止，false则等待任务结束后再停止
        System.out.println("终止订单线程池+调度线程池：" + scheduledFuture.cancel(false));
        scheduler.shutdown();
        threadPool.shutdown();
    }

    protected abstract void init();
}
