package net.doge.ui;

import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;

public class UIPreProcessor {
    // 全局字体抗锯齿，必须在初始化 UIManager 之前调用！
    private static void enableAntiAliasing() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    // 初始化 UI 管理器配置
    private static void initUIManager() {
        // 列表不按照行块为单位滚动，提升动画流畅性
        UIManager.put("List.lockToPositionOnScroll", false);
        UIManager.put("TabbedPane.tabInsets", new InsetsUIResource(0, 4, 0, 4));
    }

    public static void process() {
        enableAntiAliasing();
        initUIManager();
    }
}
