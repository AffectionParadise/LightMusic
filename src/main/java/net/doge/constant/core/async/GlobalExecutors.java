package net.doge.constant.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Doge
 * @description 线程池
 * @date 2020/12/12
 */
public class GlobalExecutors {
    // 下载任务(大小由设置决定)
    public static ThreadPoolExecutor downloadExecutor;
    // 请求
    public static final ThreadPoolExecutor requestExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);
    // 图片加载
    public static final ThreadPoolExecutor imageExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
}
