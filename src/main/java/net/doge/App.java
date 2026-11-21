package net.doge;

import net.doge.ui.MainFrame;
import net.doge.util.core.log.LogUtil;

/**
 * @author Doge
 * @description 启动类
 * @date 2020/12/12
 */
public class App {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> LogUtil.error("线程出现未捕获异常", error));
        MainFrame.launch();
    }
}
