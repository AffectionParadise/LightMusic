package net.doge.util.core.os;

import cn.hutool.core.util.RuntimeUtil;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.util.core.io.IoUtil;
import net.doge.util.core.log.LogUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author Doge
 * @description 终端工具类
 * @date 2020/12/15
 */
public class TerminalUtil {
    /**
     * 执行命令
     *
     * @param commands
     * @return
     */
    public static void exec(String... commands) {
        Process p = RuntimeUtil.exec(commands);
        IoUtil.drainAsync(p.getInputStream());
        IoUtil.drainAsync(p.getErrorStream());
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            LogUtil.error(e);
        } finally {
            p.destroy();
        }
    }

    /**
     * 异步执行命令
     *
     * @param commands
     * @return
     */
    public static void execAsync(String... commands) {
        GlobalExecutors.requestExecutor.execute(() -> exec(commands));
    }

    /**
     * 执行命令，获取标准输出字符串
     *
     * @param commands
     */
    public static String execAsStr(String... commands) {
        return RuntimeUtil.execForStr(StandardCharsets.UTF_8, commands);
    }
}
