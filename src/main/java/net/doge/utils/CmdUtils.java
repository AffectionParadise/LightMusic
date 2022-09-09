package net.doge.utils;

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
    private static String[] dirs = {"MiguMusicApi", "NeteaseCloudMusicApi", "QQMusicApi", "kuwoMusicApi"};
    private static String[] cmds = {"npm start", "npm start", "npm start", "npm run dev"};

    public static void startSvc() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        for (int i = 0, l = dirs.length; i < l; i++) {
            System.setProperty("user.dir", dirs[i]);
            p = rt.exec(new String[]{"cmd", "/c", cmds[i]}, null, null);
            System.setProperty("user.dir", "..");
        }
        p.waitFor();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        startSvc();
    }
}
