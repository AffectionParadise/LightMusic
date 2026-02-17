package net.doge.sdk.common.entity.executor;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.collection.ListUtil;
import net.doge.util.core.exception.ExceptionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多请求任务执行管理器
 */
public class MultiCommonResultCallableExecutor<T> {
    private List<Future<CommonResult<T>>> taskList = new LinkedList<>();

    // 提交任务
    public void submit(Callable<CommonResult<T>> callable) {
        taskList.add(GlobalExecutors.requestExecutor.submit(callable));
    }

    // 同步等待所有任务完成并返回结果
    public CommonResult<T> getResult() {
        List<List<T>> rl = new LinkedList<>();
        AtomicInteger total = new AtomicInteger(0);
        taskList.forEach(task -> {
            try {
                CommonResult<T> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (Exception e) {
                ExceptionUtil.handleAsyncException(e);
            }
        });
        List<T> res = ListUtil.joinAll(rl);
        return new CommonResult<>(res, total.get());
    }
}
