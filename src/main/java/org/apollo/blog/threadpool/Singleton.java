package org.apollo.blog.threadpool;

import org.apollo.blog.threadpool.only.OnlyThreadPoolManager;

public enum Singleton {
    /**
     * 单例模式
     */
    INSTANCE {
        @Override
        protected OnlyThreadPoolManager init() {
            return new OnlyThreadPoolManager();
        }
    };

    public static OnlyThreadPoolManager getInstance() {
        return Singleton.INSTANCE.init();
    }

    protected abstract OnlyThreadPoolManager init();
}
