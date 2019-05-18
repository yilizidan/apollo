package org.apollo.blog.threadpool.only;

public abstract class OnlyRunnable implements Runnable {
    public void setT(String t) {
        this.t = t;
    }

    private String t;

    public String getT() {
        return t;
    }

    public void setOnlyThreadPoolManager(OnlyThreadPoolManager onlyThreadPoolManager) {
        this.onlyThreadPoolManager = onlyThreadPoolManager;
    }

    private OnlyThreadPoolManager onlyThreadPoolManager;

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
        } finally {
            onlyThreadPoolManager.removeQueue(t);
        }

    }

    protected abstract void execute() throws Exception;
}
