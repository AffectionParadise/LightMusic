package net.doge.ui.componentui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 选项卡面板自定义 UI
 * @Date 2020/12/13
 */
public class TabbedPaneUI extends BasicTabbedPaneUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        try {
            super.paint(g, c);
        } catch (Exception e) {

        }
    }
//
//    @Override
//    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
//        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
//    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
//        super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
//        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
//        super.paintContentBorder(g, tabPlacement, selectedIndex);
    }
}
