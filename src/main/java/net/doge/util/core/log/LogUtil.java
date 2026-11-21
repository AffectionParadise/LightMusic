package net.doge.util.core.log;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.dialect.console.ConsoleLog;
import cn.hutool.log.level.Level;

/**
 * @author Doge
 * @description 日志工具类
 * @date 2020/12/15
 */
public class LogUtil {
    static {
        // 全局日志等级
        ConsoleLog.setLevel(Level.ERROR);
    }

    public static void info(Throwable throwable) {
        log(Level.INFO, "诊断信息", throwable);
    }

    public static void error(Throwable throwable) {
        error("操作失败", throwable);
    }

    public static void error(String operation, Throwable throwable) {
        log(Level.ERROR, operation, throwable);
    }

    // 控制台分别写入标题和堆栈，串行输出可避免本工具的多条异常日志交错
    private static synchronized void log(Level level, String operation, Throwable throwable) {
        if (throwable == null) return;
        Thread thread = Thread.currentThread();
        String loggerClass = LogUtil.class.getName();
        for (StackTraceElement frame : thread.getStackTrace()) {
            String className = frame.getClassName();
            if (className.equals(loggerClass) || className.equals(Thread.class.getName())) continue;
            Log logger = LogFactory.get(className);
            // 第一个参数 fqcn 解释：忽略掉名为 callerFQCN 的栈帧，把紧接着它的上一帧识别为真正的调用者
            logger.log(loggerClass, level, throwable, "[{}] {}:{} {}", thread.getName(),
                    frame.getMethodName(), frame.getLineNumber(), operation);
            return;
        }
        LogFactory.get(loggerClass).log(loggerClass, level, throwable, "[{}] {}", thread.getName(), operation);
    }
}
