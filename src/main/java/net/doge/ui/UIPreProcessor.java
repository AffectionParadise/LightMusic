package net.doge.ui;

import net.doge.ui.core.resource.HDInsetsUIResource;
import net.doge.util.core.LogUtil;

import javax.swing.*;

public class UIPreProcessor {
    // 虚拟机图形参数
    private static void initGraphicsConfig() {
        // 全局字体抗锯齿，必须在初始化 UIManager 之前调用！
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // jdk 9 以上默认适配 dpi，会有图像绘制模糊问题，设置为关闭
//        System.setProperty("sun.java2d.uiScale", "1.0");
//        // 启用 OpenGL 硬件加速
//        System.setProperty("sun.java2d.opengl", "true");
//        // 对于 Windows，启用 Direct3D
//        System.setProperty("sun.java2d.d3d", "true");
//        // 禁用 DirectDraw(在某些情况下可能有问题)
//        System.setProperty("sun.java2d.noddraw", "true");

//        // Swing 的事件调度线程(EDT)
//        // 禁用新的线程调度策略，使用旧的 FIFO 策略
//        System.setProperty("java.awt.EventQueue.class", "sun.awt.EventQueue");
//        // 设置事件队列的优先级
//        System.setProperty("sun.awt.eventqueue.anon", "true");
//        // 禁用中断检测(可能有助于减少延迟)
//        System.setProperty("sun.awt.noerasebackground", "true");
    }

    // 统一 LAF，避免界面元素混乱
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
        UIManager.put("TabbedPane.tabInsets", new HDInsetsUIResource(0, 4, 0, 4));
    }

    public static void process() {
        initGraphicsConfig();
        initLAF();
        initUIManagerConfig();
    }
}
