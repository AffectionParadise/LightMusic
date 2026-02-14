package net.doge.util.os;

import cn.hutool.core.util.RuntimeUtil;
import lombok.AllArgsConstructor;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.util.core.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author Doge
 * @Description 终端工具类
 * @Date 2020/12/15
 */
public class TerminalUtil {
    /**
     * 执行命令(同步)
     *
     * @param command
     */
    public static void execSync(String command) {
        try {
            Process p = RuntimeUtil.exec(command);
            // 获取外部程序标准输出流
            GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getInputStream(), false));
            // 获取外部程序标准错误流
            GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getErrorStream(), true));
            p.waitFor();
        } catch (InterruptedException e) {
            LogUtil.error(e);
        }
    }

    /**
     * 执行命令(同步)，获取标准输出字符串
     *
     * @param command
     */
    public static String execAsStr(String command) {
        Process p = RuntimeUtil.exec(command);
        // 获取外部程序标准错误流
        GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getErrorStream(), true));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (IOException e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 执行命令(异步)
     *
     * @param command
     * @return
     */
    public static void exec(String command) {
        RuntimeUtil.exec(command);
    }

    @AllArgsConstructor
    private static class OutputHandlerRunnable implements Runnable {
        private InputStream in;
        private boolean allowOutput;

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null)
                    if (allowOutput) System.out.println(line);
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
    }
}
