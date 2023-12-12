package net.doge.util.system;

import com.github.houbb.heaven.util.util.OsUtil;

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
        if (OsUtil.isWindows()) TerminalUtil.exec("explorer /select, " + path);
        else if (OsUtil.isMac()) TerminalUtil.exec("open " + path);
        else if (OsUtil.isUnix()) TerminalUtil.exec("nautilus " + path);
    }

    /**
     * 编辑文本
     *
     * @param path
     * @return
     */
    public static void edit(String path) {
        if (OsUtil.isWindows()) TerminalUtil.exec("notepad " + path);
        else if (OsUtil.isMac()) TerminalUtil.exec("open -e " + path);
        else if (OsUtil.isUnix()) TerminalUtil.exec("vim " + path);
    }
}
