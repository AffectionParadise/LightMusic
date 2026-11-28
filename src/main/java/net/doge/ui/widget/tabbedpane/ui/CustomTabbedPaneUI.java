package net.doge.ui.widget.tabbedpane.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * @author Doge
 * @description 选项卡面板自定义 UI
 * @date 2020/12/13
 */
public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
    private boolean isHiddenVerticalTab(int tabPlacement, int index) {
        if ((tabPlacement != LEFT && tabPlacement != RIGHT) || index < 0) return false;
        Component header = tabPane.getTabComponentAt(index);
        return header != null && !header.isVisible();
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        if (isHiddenVerticalTab(tabPlacement, tabIndex)) return 0;
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
    }

    @Override
    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        if (isHiddenVerticalTab(tabPlacement, tabPane.getSelectedIndex())) return new Insets(0, 0, 0, 0);
        return super.getSelectedTabPadInsets(tabPlacement);
    }

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
