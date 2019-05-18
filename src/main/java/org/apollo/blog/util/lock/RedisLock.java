package org.apollo.blog.util.lock;

public interface RedisLock  extends AutoCloseable {
	/**
     * 释放分布式锁
     */
    void unlock();
}
