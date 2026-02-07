package net.doge.ui.widget.tabbedpane.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 选项卡面板自定义 UI
 * @Date 2020/12/13
 */
public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        try {
            super.paint(g, c);
        } catch (Exception e) {

        }
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // 不绘制标签边框
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // 不绘制标签背景
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        // 不绘制内容背景
    }
}
