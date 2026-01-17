package net.doge.ui;

import net.doge.util.common.LogUtil;

import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;

public class UIPreProcessor {
    // 虚拟机图形参数
    private static void initGraphicsConfig() {
        // 全局字体抗锯齿，必须在初始化 UIManager 之前调用！
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        // opengl 加速
        System.setProperty("sun.java2d.opengl", "true");
        // jdk 9 以上默认适配 dpi，会有图像绘制模糊问题，设置为关闭
//        System.setProperty("sun.java2d.uiScale", "1.0");
    }

    // 统一 LAF 为 Metal，避免界面元素混乱
    private static void initLAF() {
        if ("Metal".equals(UIManager.getLookAndFeel().getName())) return;
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            LogUtil.error(e);
        }
    }

    // 初始化 UI 管理器配置
    private static void initUIManagerConfig() {
        // 列表不按照行块为单位滚动，提升动画流畅性
        UIManager.put("List.lockToPositionOnScroll", false);
        UIManager.put("TabbedPane.tabInsets", new InsetsUIResource(0, 4, 0, 4));
    }

    public static void process() {
        initGraphicsConfig();
        initLAF();
        initUIManagerConfig();
    }
}
