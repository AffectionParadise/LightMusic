package net.doge.ui.components;

import javax.swing.*;

/**
 * @Author yzx
 * @Description 自定义标签页面板
 * @Date 2020/12/13
 */
public class CustomTabbedPane extends JTabbedPane {

    public CustomTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        setFocusable(false);
    }
}
