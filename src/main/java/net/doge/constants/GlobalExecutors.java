package net.doge.constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author yzx
 * @Description 线程池
 * @Date 2020/12/12
 */

public class GlobalExecutors {
    // 下载任务
    public static ExecutorService downloadExecutor = Executors.newFixedThreadPool(5);
    // 请求
    public static ExecutorService requestExecutor = Executors.newFixedThreadPool(12);
    // 图片加载
    public static ExecutorService imageExecutor = Executors.newFixedThreadPool(8);
}
