package net.doge.sdk.common.entity.executor;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.util.core.exception.ExceptionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 多请求任务执行管理器
 */
public class MultiRunnableExecutor {
    private List<Future<?>> taskList = new LinkedList<>();

    // 提交任务
    public void submit(Runnable runnable) {
        taskList.add(GlobalExecutors.requestExecutor.submit(runnable));
    }

    // 同步等待所有任务完成
    public void await() {
        taskList.forEach(task -> {
            try {
                task.get();
            } catch (Exception e) {
                ExceptionUtil.handleAsyncException(e);
            }
        });
    }
}
