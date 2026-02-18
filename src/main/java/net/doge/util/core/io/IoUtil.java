package net.doge.util.core.io;

import net.doge.constant.core.async.GlobalExecutors;

import java.io.InputStream;

/**
 * @author Doge
 * @description IO 工具类
 * @date 2020/12/21
 */
public class IoUtil {
    /**
     * 排空一个流
     *
     * @param in
     */
    public static void drain(InputStream in) {
        if (in == null) return;
        cn.hutool.core.io.IoUtil.readBytes(in);
    }

    /**
     * 异步排空一个流
     *
     * @param in
     */
    public static void drainAsync(InputStream in) {
        if (in == null) return;
        GlobalExecutors.requestExecutor.execute(() -> drain(in));
    }
}
