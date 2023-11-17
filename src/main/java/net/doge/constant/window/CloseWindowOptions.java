package net.doge.constant.window;

import net.doge.constant.lang.I18n;

/**
 * @Author Doge
 * @Description 关闭窗口操作
 * @Date 2020/12/7
 */
public class CloseWindowOptions {
    public static final int ASK = 0;
    public static final int DISPOSE = 1;
    public static final int EXIT = 2;

    public static final String[] NAMES = {
            I18n.getText("closingAsk"),
            I18n.getText("closingHide"),
            I18n.getText("closingExit")
    };
}
