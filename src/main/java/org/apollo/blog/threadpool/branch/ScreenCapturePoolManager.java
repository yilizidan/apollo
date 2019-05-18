package org.apollo.blog.threadpool.branch;

import org.apollo.blog.threadpool.base.BaseThreadPoolManager;
import org.springframework.stereotype.Component;

@Component
public class ScreenCapturePoolManager extends BaseThreadPoolManager {
    @Override
    protected void init() {
        super.setCorePoolSize(2);
        super.setMaxPoolSize(5);
    }
}
