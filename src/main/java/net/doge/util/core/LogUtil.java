package net.doge.util.core;

import cn.hutool.log.StaticLog;
import cn.hutool.log.dialect.console.ConsoleLog;
import cn.hutool.log.level.Level;

/**
 * @Author Doge
 * @Description 日志工具类
 * @Date 2020/12/15
 */
public class LogUtil {
    static {
        // 全局日志等级
        ConsoleLog.setLevel(Level.ERROR);
    }

    /**
     * 输出 INFO 日志
     *
     * @param throwable
     * @return
     */
    public static void info(Throwable throwable) {
        log(Level.INFO, throwable, "");
    }

    /**
     * 输出 ERROR 日志
     *
     * @param throwable
     * @return
     */
    public static void error(Throwable throwable) {
        log(Level.ERROR, throwable, "");
    }

    private static void log(Level level, Throwable throwable, String format, Object... params) {
        StaticLog.log(level, throwable, format, params);
    }
}
