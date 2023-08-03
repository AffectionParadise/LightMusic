package net.doge.util.system;

import cn.hutool.core.util.RuntimeUtil;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.meta.SoftInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author Doge
 * @Description cmd 工具类
 * @Date 2020/12/15
 */
public class TerminateUtil {
    /**
     * 执行命令(同步)
     *
     * @param command
     * @return
     */
    public static int exec(String command) {
        try {
            Process p = RuntimeUtil.exec(command);
            // 获取外部程序标准输出流
            GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getInputStream(), false));
            // 获取外部程序标准错误流
            GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getErrorStream(), true));
            int code = p.waitFor();
            return code;
        } catch (InterruptedException e) {
            return -1;
        }
    }

    /**
     * 执行命令(异步)
     *
     * @param command
     * @return
     */
    public static void execAsync(String command) {
        RuntimeUtil.exec(command);
    }

    /**
     * 调用更新程序
     *
     * @param
     * @return
     */
    public static void updater() {
        execAsync(SoftInfo.UPDATER_FILE_NAME + " 54ee8fdb2c9f213c4e4eea81268fc38b");
    }

    /**
     * 调用文件资源管理器
     *
     * @param
     * @return
     */
    public static void explorer(String path) {
        execAsync(String.format("explorer /select, \"%s\"", path));
    }

    /**
     * 调用记事本
     *
     * @param
     * @return
     */
    public static void notepad(String path) {
        execAsync(String.format("notepad \"%s\"", path));
    }

    private static class OutputHandlerRunnable implements Runnable {
        private InputStream in;
        private boolean error;

        public OutputHandlerRunnable(InputStream in, boolean error) {
            this.in = in;
            this.error = error;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (error) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
