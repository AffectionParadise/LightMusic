package net.doge.ui.component.tabbedpane;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义标签页面板
 * @Date 2020/12/13
 */
public class CustomTabbedPane extends JTabbedPane {
    public CustomTabbedPane(int tabPlacement) {
        super(tabPlacement, SCROLL_TAB_LAYOUT);
        setFocusable(false);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        super.setTabComponentAt(index, component);
        // 设置标签卡手势
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
