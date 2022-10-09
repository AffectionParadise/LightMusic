package net.doge.utils;

import cn.hutool.core.util.RuntimeUtil;
import net.doge.constants.GlobalExecutors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.List;

/**
 * @Author yzx
 * @Description cmd 工具类
 * @Date 2020/12/15
 */
public class CmdUtils {
//    private static String[] dirs = {"MiguMusicApi", "NeteaseCloudMusicApi", "QQMusicApi", "kuwoMusicApi"};
//    private static String[] cmds = {"npm start", "npm start", "npm start", "npm run dev"};
//
//    public static void startSvc() throws IOException, InterruptedException {
//        Runtime rt = Runtime.getRuntime();
//        Process p = null;
//        for (int i = 0, l = dirs.length; i < l; i++) {
//            System.setProperty("user.dir", dirs[i]);
//            p = rt.exec(new String[]{"cmd", "/c", cmds[i]}, null, null);
//            System.setProperty("user.dir", "..");
//        }
//        p.waitFor();
//    }
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        startSvc();
//    }

    /**
     * 执行命令
     *
     * @param command
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static int exec(String command) throws IOException, InterruptedException {
        Process p = RuntimeUtil.exec(command);
        // 获取外部程序标准输出流
        GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getInputStream(), false));
        // 获取外部程序标准错误流
        GlobalExecutors.requestExecutor.execute(new OutputHandlerRunnable(p.getErrorStream(), true));
        int code = p.waitFor();
        return code;
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
            try (BufferedReader bufr = new BufferedReader(new InputStreamReader(in))) {
                String line = null;
                while ((line = bufr.readLine()) != null) {
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
