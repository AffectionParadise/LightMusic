package net.doge.util.core.async;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步工具类
 */
public class AsyncUtil {
    /**
     * 清空线程池阻塞队列
     *
     * @param executor
     */
    public static void clearBlockingQueue(ThreadPoolExecutor executor) {
        if (executor == null) return;
        executor.getQueue().clear();
    }
}
