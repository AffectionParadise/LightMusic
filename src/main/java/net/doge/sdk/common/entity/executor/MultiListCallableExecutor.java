package net.doge.sdk.common.entity.executor;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.util.core.exception.ExceptionUtil;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 多请求任务执行管理器
 */
public class MultiListCallableExecutor<T> {
    private List<Future<List<T>>> taskList = new LinkedList<>();

    // 提交任务
    public void submit(Callable<List<T>> callable) {
        taskList.add(GlobalExecutors.requestExecutor.submit(callable));
    }

    // 同步等待所有任务完成并返回结果
    public Set<T> getResultAsSet() {
        Set<T> res = new LinkedHashSet<>();
        taskList.forEach(task -> {
            try {
                res.addAll(task.get());
            } catch (Exception e) {
                ExceptionUtil.handleAsyncException(e);
            }
        });
        return res;
    }
}
