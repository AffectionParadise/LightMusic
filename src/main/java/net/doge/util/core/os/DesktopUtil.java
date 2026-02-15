package net.doge.util.core.os;

import com.github.houbb.heaven.util.util.OsUtil;
import net.doge.constant.core.meta.SoftInfo;
import net.doge.constant.core.os.SimplePath;
import net.doge.util.core.log.LogUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author Doge
 * @Description 桌面工具类
 * @Date 2020/12/15
 */
public class DesktopUtil {
    /**
     * 浏览器打开 url
     *
     * @param url
     * @return
     */
    public static void browse(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(url);
            desktop.browse(uri);
        } catch (IOException | URISyntaxException e) {
            LogUtil.error(e);
        }
    }

    /**
     * 打开资源管理器
     *
     * @param file
     * @return
     */
    public static void explore(File file) {
        String path = file.getAbsolutePath();
        if (OsUtil.isWindows()) TerminalUtil.exec(String.format("explorer /select, \"%s\"", path));
        else if (OsUtil.isMac()) TerminalUtil.exec(String.format("open \"%s\"", path));
        else if (OsUtil.isUnix()) TerminalUtil.exec(String.format("nautilus \"%s\"", path));
    }

    /**
     * 编辑文本
     *
     * @param path
     * @return
     */
    public static void edit(String path) {
        if (OsUtil.isWindows()) TerminalUtil.exec(String.format("notepad \"%s\"", path));
        else if (OsUtil.isMac()) TerminalUtil.exec(String.format("open -e \"%s\"", path));
        else if (OsUtil.isUnix()) TerminalUtil.exec(String.format("vim \"%s\"", path));
    }

    /**
     * 调用更新程序
     *
     * @param
     * @return
     */
    public static void updater(String keyMD5) {
        TerminalUtil.exec(SoftInfo.UPDATER_FILE_NAME + " " + keyMD5);
    }

    /**
     * 发送模拟浏览器的请求
     *
     * @param
     * @return
     */
    public static String impersonateGet(String url) {
        return impersonateGet(url, "chrome131");
    }

    public static String impersonateGet(String url, String impersonate) {
        return TerminalUtil.execAsStr(SimplePath.PLUGIN_PATH + String.format("curl-impersonate \"%s\" \"%s\"", url, impersonate));
    }
}
