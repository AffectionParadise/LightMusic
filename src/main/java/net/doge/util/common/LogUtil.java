package net.doge.util.common;

/**
 * @Author Doge
 * @Description 日志工具类
 * @Date 2020/12/15
 */
public class LogUtil {
    // 颜色
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";

    // 级别
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";

    /**
     * 输出 INFO 日志
     *
     * @param throwable
     * @return
     */
    public static void info(Throwable throwable) {
        log(INFO, throwable);
    }

    /**
     * 输出错误日志
     *
     * @param throwable
     * @return
     */
    public static void error(Throwable throwable) {
        log(ERROR, throwable);
    }

    private static void log(String level, Throwable throwable) {
        StackTraceElement trace = throwable.getStackTrace()[0];
        String fileName = trace.getFileName();
        int lineNumber = trace.getLineNumber();
        String datetime = TimeUtil.msToDatetime(System.currentTimeMillis());
        String msg = throwable.toString();
        String color = "";
        switch (level) {
            case ERROR:
                color = RED;
                break;
            case INFO:
                color = GREEN;
                break;
        }
        System.out.printf("%s[%s] [%s] (%s:%s) %s%s%n",
                color, datetime, level, fileName, lineNumber, msg, RESET);
    }
}
